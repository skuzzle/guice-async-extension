package de.skuzzle.inject.async.internal.runnables;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.ScheduledContext;

class ReScheduleRunnable implements Reschedulable {

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
        this.invocation.run();
    }

    @Override
    public void scheduleNextExecution() {
        final ZonedDateTime now = ZonedDateTime.now();
        final Duration timeToNext = this.executionTime.timeToNextExecution(now);
        final long delay = timeToNext.toMillis();

        final LockableRunnable locked = new LatchLockableRunnable(this);

        try {
            final Future<?> future = this.executor.schedule(locked, delay,
                    TimeUnit.MILLISECONDS);
            this.context.setFuture(future);
        } finally {
            locked.release();
        }

    }
}
