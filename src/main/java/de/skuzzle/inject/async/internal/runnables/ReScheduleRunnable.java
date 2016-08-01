package de.skuzzle.inject.async.internal.runnables;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Duration;

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
        final DateTime now = DateTime.now();
        final Duration timeToNext = this.executionTime.timeToNextExecution(now);
        final long delay = timeToNext.getMillis();

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
