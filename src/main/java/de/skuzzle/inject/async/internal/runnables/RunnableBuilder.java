package de.skuzzle.inject.async.internal.runnables;

import java.util.concurrent.ScheduledExecutorService;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

public interface RunnableBuilder {

    Runnable scope(Runnable unscope, ScheduledContext context);

    Runnable invoke(InjectedMethodInvocation invocation);

    /**
     * Creates a runnable that reschedules itself with the provided scheduler before
     * actually executing the given invocation.
     *
     * @param wrapped The actual invocation.
     * @param scheduler The scheduler for rescheduling.
     * @param executionTime For figuring out the delay until the next execution.
     * @return The runnable.
     */
    Reschedulable reschedule(Runnable wrapped, ScheduledExecutorService scheduler,
            ExecutionTime executionTime);
}
