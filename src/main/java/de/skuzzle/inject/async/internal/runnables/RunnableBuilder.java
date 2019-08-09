package de.skuzzle.inject.async.internal.runnables;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ExecutionContext;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

/**
 * May be used to construct different types of {@link Runnable runnables}. Those are
 * useful for implementing {@link TriggerStrategy trigger strategies}. For simple strategy
 * implementations it might be useful to only use
 * {@link #createRunnableStack(InjectedMethodInvocation, ScheduledContext, ExceptionHandler)}
 * for obtaining a runnable that can be scheduled some how.
 *
 * @author Simon Taddiken
 * @since 0.3.0
 */
public interface RunnableBuilder {

    /**
     * Like
     * {@link #createRunnableStack(InjectedMethodInvocation, ScheduledContext, ExceptionHandler)}
     * but creates a Runnable which <b>must</b> be {@link LockableRunnable#release()
     * released} before it is able to be executed.
     *
     * @param invocation The invocation to execute.
     * @param context The context of the execution.
     * @param handler The error handler.
     * @return The runnable.
     */
    LockableRunnable createLockedRunnableStack(InjectedMethodInvocation invocation,
            ScheduledContext context, ExceptionHandler handler);

    /**
     * Creates a runnable which wraps around another runnable and executes the wrapped
     * instance in the scope of the given {@link ExecutionContext}. That is,
     * {@link ScheduledContext#beginNewExecution()} is called before- and
     * {@link ScheduledContext#finishExecution()} is called after the execution.
     *
     * @param unscoped The unscoped runnable.
     * @param context The context in which the unscoped runnable will be executed.
     * @return The runnable.
     */
    Runnable scope(Runnable unscoped, ScheduledContext context);

    /**
     * Creates a {@link Runnable} which will execute the given invocation.
     *
     * @param invocation The invocation.
     * @return The runnable.
     */
    Runnable invoke(InjectedMethodInvocation invocation);

    /**
     * Creates a {@link Runnable} that wraps around the given one and which delegates
     * excptions thrown by the inner runnable to the given handler.
     *
     * @param wrapped The wrapped runnable.
     * @param handler The exception handler.
     * @return The runnable.
     */
    Runnable handleException(Runnable wrapped, ExceptionHandler handler);
}
