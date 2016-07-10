package de.skuzzle.inject.async.internal.runnables;

import java.util.concurrent.ScheduledExecutorService;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

class RunnableBuilderImpl implements RunnableBuilder {

    @Override
    public Runnable scope(Runnable unscoped, ScheduledContext context) {
        return ScopedRunnable.of(unscoped, context);
    }

    @Override
    public Runnable invoke(InjectedMethodInvocation invocation) {
        return InvokeMethodRunnable.of(invocation);
    }


    @Override
    public Reschedulable reschedule(Runnable wrapped, ScheduledExecutorService scheduler,
            ExecutionTime executionTime) {
        return ReScheduleRunnable.of(wrapped, scheduler, executionTime);
    }

}
