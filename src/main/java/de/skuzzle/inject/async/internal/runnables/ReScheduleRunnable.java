package de.skuzzle.inject.async.internal.runnables;

import java.time.Duration;
import java.time.ZonedDateTime;
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

        final ZonedDateTime now = ZonedDateTime.now();
        final Duration timeToNext = this.executionTime.timeToNextExecution(now)
                .orElseThrow(() -> new IllegalStateException("Could not determine time to next execution"));
        final long delay = timeToNext.toMillis();
        LOG.trace("Calculated ms from now {} until next execution: {}", now, delay);

        // This construct makes sure that the 'Future' that is obtained from scheduling
        // the task is published to the 'ScheduledContext' before the task is actually
        // executed.
        final LockableRunnable locked = new LatchLockableRunnable(this);
        try {
            final Future<?> future = this.executor.schedule(locked, delay, TimeUnit.MILLISECONDS);
            this.context.setFuture(future);
        } finally {
            locked.release();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("invocation", invocation)
                .add("context", context)
                .toString();
    }
}
