package de.skuzzle.inject.async.internal.runnables;

import java.util.concurrent.ScheduledExecutorService;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.internal.context.ScheduledContextImpl;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

public interface RunnableBuilder {

    Runnable scope(Runnable unscope, ScheduledContextImpl context);

    Runnable invoke(InjectedMethodInvocation invocation);

    /**
     * Creates a runnable which skips execution if the given context has been requested to
     * stop execution. Otherwise the wrapped runnable is executed.
     * 
     * @param wrapped The wrapped runnable.
     * @param context The context.
     * @return The runnable.
     * @see ScheduledContext#isStopRequested()
     */
    Runnable skip(Runnable wrapped, ScheduledContextImpl context);

    /**
     * Creates a runnable that reschedules itself with the provided scheduler before
     * actually executing the given invocation.
     * 
     * @param wrapped The actual invocation.
     * @param scheduler The scheduler for rescheduling.
     * @param executionTime For figuring out the delay until the next execution.
     * @param context The context.
     * @return The runnable.
     */
    Reschedulable reschedule(Runnable wrapped, ScheduledExecutorService scheduler,
            ExecutionTime executionTime, ScheduledContextImpl context);
}
