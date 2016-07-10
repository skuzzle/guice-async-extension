package de.skuzzle.inject.async.internal.runnables;

import java.util.concurrent.ScheduledExecutorService;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.internal.context.ScheduledContextImpl;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

class RunnableBuilderImpl implements RunnableBuilder {

    @Override
    public Runnable scope(Runnable unscoped, ScheduledContextImpl context) {
        return ScopedRunnable.of(unscoped, context);
    }

    @Override
    public Runnable invoke(InjectedMethodInvocation invocation) {
        return InvokeMethodRunnable.of(invocation);
    }
    
    @Override
    public Runnable skip(Runnable wrapped, ScheduledContextImpl context) {
        return SkipRunnable.of(wrapped, context);
    }

    @Override
    public Reschedulable reschedule(Runnable wrapped, ScheduledExecutorService scheduler, 
            ExecutionTime executionTime, ScheduledContextImpl context) {
        return ReScheduleRunnable.of(wrapped, scheduler, executionTime, context);
    }

}
