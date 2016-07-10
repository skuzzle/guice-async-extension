package de.skuzzle.inject.async.internal.runnables;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.internal.context.ScheduledContextImpl;

class ReScheduleRunnable implements Reschedulable {

    private final Runnable invocation;
    private final ScheduledExecutorService executor;
    private final ExecutionTime executionTime;
    private final ScheduledContextImpl context;

    private ReScheduleRunnable(Runnable invocation,
            ScheduledExecutorService executor, ExecutionTime executionTime,
            ScheduledContextImpl context) {
        this.invocation = invocation;
        this.executor = executor;
        this.executionTime = executionTime;
        this.context = context;
    }

    static Reschedulable of(Runnable invocation, ScheduledExecutorService scheduler,
            ExecutionTime executionTime, ScheduledContextImpl context) {
        return new ReScheduleRunnable(invocation, scheduler, executionTime, context);
    }

    @Override
    public void run() {
        if (!context.isStopRequested()) {
            scheduleNextExecution();
            invocation.run();
        }
    }

    @Override
    public void scheduleNextExecution() {
        final DateTime now = DateTime.now();
        final Duration timeToNext = this.executionTime.timeToNextExecution(now);
        final long delay = timeToNext.getMillis();
        this.executor.schedule(this, delay, TimeUnit.MILLISECONDS);
    }
}
