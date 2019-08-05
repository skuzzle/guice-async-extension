package de.skuzzle.inject.async.internal.runnables;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cronutils.model.time.ExecutionTime;
import com.google.common.base.MoreObjects;

import de.skuzzle.inject.async.ScheduledContext;

class ReScheduleRunnable implements Reschedulable {

    private static final Logger LOG = LoggerFactory.getLogger(ReScheduleRunnable.class);

    private final Runnable invocation;
    private final ScheduledExecutorService executor;
    private final ExecutionTime executionTime;
    private final ScheduledContext context;

    // The time at which this Runnable will be called again by the Scheduler. This
    // reference is only null until the first call to #scheduleNextExecution. This
    // reference is guarded by the monitor of this instance to guarantee thread safe
    // updates
    private ZonedDateTime expectedNextExecution = null;

    private ReScheduleRunnable(ScheduledContext context, Runnable invocation,
            ScheduledExecutorService executor, ExecutionTime executionTime) {
        this.context = context;
        this.invocation = invocation;
        this.executor = executor;
        this.executionTime = executionTime;
    }

    static Reschedulable of(ScheduledContext context, Runnable invocation,
            ScheduledExecutorService scheduler,
            ExecutionTime executionTime) {
        return new ReScheduleRunnable(context, invocation, scheduler, executionTime);
    }

    @Override
    public void run() {
        scheduleNextExecution();
        LOG.debug("Executing actual invocation: {}", invocation);
        this.invocation.run();
    }

    @Override
    public void scheduleNextExecution() {
        LOG.debug("Scheduling next invocation of {}", invocation);

        final long delayUntilNextExecution = millisUntilNextExecution();

        // This construct makes sure that the 'Future' that is obtained from scheduling
        // the task is published to the 'ScheduledContext' before the task is actually
        // executed.
        final LockableRunnable locked = new LatchLockableRunnable(this);
        try {
            final Future<?> future = this.executor.schedule(locked, delayUntilNextExecution, TimeUnit.MILLISECONDS);
            this.context.setFuture(future);
        } finally {
            locked.release();
        }
    }

    private synchronized long millisUntilNextExecution() {
        // The base date from which the delay until the next execution will be calculated.
        final ZonedDateTime currentExecution = currentExecutionTime();
        final ZonedDateTime now = ZonedDateTime.now();

        final long inaccuracy = ChronoUnit.MILLIS.between(currentExecution, now);
        LOG.trace("cron scheduler inaccuracy: {} ms", inaccuracy);

        final ZonedDateTime nextExecution = this.executionTime.nextExecution(currentExecution)
                .orElseThrow(() -> new IllegalStateException("Could not determine next execution time"));
        final long dealyUntilNextExecution = ChronoUnit.MILLIS.between(currentExecution, nextExecution);

        expectNextExecutionAt(nextExecution);
        LOG.trace("delay until next execution: {} ms (from '{}' to '{}')", dealyUntilNextExecution, currentExecution,
                nextExecution);
        return dealyUntilNextExecution;
    }

    private ZonedDateTime currentExecutionTime() {
        return this.expectedNextExecution == null
                ? ZonedDateTime.now()
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
