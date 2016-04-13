package de.skuzzle.inject.async.internal.trigger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.util.InjectedMethodInvocation;

class ReScheduleRunnable implements Runnable {

    private final InjectedMethodInvocation invocation;
    private final ScheduledExecutorService executor;
    private final ExecutionTime executionTime;

    ReScheduleRunnable(InjectedMethodInvocation invocation,
            ScheduledExecutorService executor, ExecutionTime executionTime) {
        this.invocation = invocation;
        this.executor = executor;
        this.executionTime = executionTime;
    }

    @Override
    public void run() {
        final DateTime now = DateTime.now();
        final Duration timeToNext = this.executionTime.timeToNextExecution(now);
        final long delay = timeToNext.getMillis();
        this.executor.schedule(this, delay, TimeUnit.MILLISECONDS);

        InvokeMethodRunnable.of(this.invocation).run();
    }
}
