package de.skuzzle.inject.async.internal.runnables;

import java.util.concurrent.ScheduledExecutorService;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

class RunnableBuilderImpl implements RunnableBuilder {

    @Override
    public LockableRunnable createLockedRunnableStack(InjectedMethodInvocation invocation,
            ScheduledContext context, ExceptionHandler handler) {
        final Runnable invokeRunnable = this.invoke(invocation);
        final Runnable errorHandler = handleException(invokeRunnable, handler);
        final Runnable scoped = this.scope(errorHandler, context);
        return new LatchLockableRunnable(scoped);
    }

    @Override
    public Runnable scope(Runnable unscoped, ScheduledContext context) {
        return ScopedRunnable.of(unscoped, context);
    }

    @Override
    public Runnable invoke(InjectedMethodInvocation invocation) {
        return InvokeMethodRunnable.of(invocation);
    }

    @Override
    public Runnable handleException(Runnable wrapped, ExceptionHandler handler) {
        return new ExceptionHandlingRunnable(wrapped, handler);
    }

    @Override
    public Reschedulable reschedule(ScheduledContext context, Runnable wrapped,
            ScheduledExecutorService scheduler, ExecutionTime executionTime) {
        return RescheduleRunnable.of(context, wrapped, scheduler, executionTime);
    }

}
