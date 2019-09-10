package de.skuzzle.inject.async.schedule.trigger;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cronutils.model.time.ExecutionTime;
import com.google.common.base.MoreObjects;

import de.skuzzle.inject.async.schedule.LockableRunnable;
import de.skuzzle.inject.async.schedule.ScheduledContext;

/**
 * Periodically schedules a Runnable with a a {@link ScheduledExecutorService} according
 * to a given cron pattern.
 * <p>
 * Prior to executing the actual action, the time until the next execution will be
 * calculated according to the provided cron pattern. The action will then be scheduled to
 * be executed again at the calculated time
 *
 * @author Simon Taddiken
 */
class CronScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(CronScheduler.class);

    private final Runnable invocation;
    private final ScheduledExecutorService executor;
    private final ExecutionTime executionTime;
    private final ScheduledContext context;

    // The time at which this Runnable will be called again by the Scheduler. This
    // reference is only null until the first call to #scheduleNextExecution. This
    // reference is guarded by the monitor of this instance to guarantee thread safe
    // updates
    private ZonedDateTime expectedNextExecution = null;

    private CronScheduler(ScheduledContext context, Runnable invocation,
            ScheduledExecutorService executor, ExecutionTime executionTime) {
        this.context = context;
        this.invocation = invocation;
        this.executor = executor;
        this.executionTime = executionTime;
    }

    static CronScheduler createWith(ScheduledContext context, Runnable invocation,
            ScheduledExecutorService scheduler,
            ExecutionTime executionTime) {
        return new CronScheduler(context, invocation, scheduler, executionTime);
    }

    public void start() {
        scheduleNextExecution();
    }

    private void scheduleNextExecution() {
        LOG.debug("Scheduling next invocation of {}", invocation);
        final long delayUntilNextExecution = millisUntilNextExecution();
        final LockableRunnable lockedRunnable = createRunnableForNextExecution();

        // This construct makes sure that the 'Future' that is obtained from scheduling
        // the task is published to the 'ScheduledContext' before the task is actually
        // executed.
        try {
            this.context.updateFuture(() -> this.executor.schedule(lockedRunnable, delayUntilNextExecution,
                    TimeUnit.MILLISECONDS));
        } finally {
            lockedRunnable.release();
        }
    }

    private LockableRunnable createRunnableForNextExecution() {
        return LockableRunnable.locked(() -> {
            scheduleNextExecution();
            LOG.debug("Executing actual invocation: {}", invocation);
            this.invocation.run();
        });
    }

    private synchronized long millisUntilNextExecution() {
        // This method is synchronized because it must atomically read/write the
        // 'expectedNextExecution' field.

        final ZonedDateTime now = ZonedDateTime.now();

        // This is the time we calculated as the 'next execution time' during the previous
        // execution. This value is very close to ZonedDateTime.now() but differences of
        // several milliseconds are possible due to scheduler inaccuracies.
        final ZonedDateTime currentExecution = expectedExecutionTimeOrElse(now);

        final long inaccuracy = ChronoUnit.MILLIS.between(currentExecution, now);
        LOG.trace("cron scheduler inaccuracy: {} ms", inaccuracy);

        final ZonedDateTime nextExecution = this.executionTime.nextExecution(currentExecution)
                .orElseThrow(() -> new IllegalStateException("Could not determine next execution time"));
        final long inaccuratDelayUntilNextExecution = ChronoUnit.MILLIS.between(currentExecution, nextExecution);
        final long accurateDelay = inaccuratDelayUntilNextExecution - inaccuracy;

        expectNextExecutionAt(nextExecution);
        LOG.trace("accurate delay until next execution: {} ms (from '{}' to '{}')",
                accurateDelay, currentExecution, nextExecution);
        return accurateDelay;
    }

    private ZonedDateTime expectedExecutionTimeOrElse(ZonedDateTime now) {
        return this.expectedNextExecution == null
                ? now
                : this.expectedNextExecution;
    }

    private void expectNextExecutionAt(ZonedDateTime date) {
        this.expectedNextExecution = date;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("invocation", invocation)
                .add("context", context)
                .add("expectedNextExecution", this.expectedNextExecution)
                .toString();
    }
}
