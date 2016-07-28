package de.skuzzle.inject.async.internal.context;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.annotation.ScheduledScope;

/**
 * Provides thread-local access to currently active {@link ScheduledContextImpl}.
 *
 * @author Simon Taddiken
 * @see ScheduledScope
 */
public class ScheduledContextHolder {

    private static final ThreadLocal<ScheduledContextImpl> STACK = new ThreadLocal<>();

    /**
     * Tests whether the current thread is currently executing a scheduled method.
     *
     * @return Whether the current thread is currently executing a scheduled method.
     */
    public static boolean isContextActive() {
        return STACK.get() != null;
    }

    /**
     * May be used to access the {@link ScheduledContext} which is currently active for
     * the current thread. Will throw an exception if no context is in place.
     *
     * @return
     */
    public static ScheduledContextImpl getContext() {
        final ScheduledContextImpl activeContext = STACK.get();
        checkState(activeContext != null, "Scope 'ScheduledScope' is currently not "
                + "active. Either there is no scheduled method being executed on the "
                + "current thread or the TriggerStrategy that scheduled the method "
                + "does not support scoped executions");
        return activeContext;
    }

    /**
     * Registers the given context to be active for the current thread. Callers must
     * ensure to also <code>pop()</code> after the execution of the scoped method is done.
     *
     * @param context The context to record as active.
     */
    public static void push(ScheduledContextImpl context) {
        checkArgument(context != null, "context may not be null. "
                + "Use .pop() to disable the currently active context.");
        final ScheduledContext activeContext = STACK.get();
        checkState(activeContext == null, "there is currently another ScheduledContext "
                + "active. There may only be one active context per thread at a time. "
                + "Currently active contet is: '%s'. Tried to set '%s' as active context",
                activeContext, context);
        STACK.set(context);
    }

    /**
     * Removes the context for the current thread.
     */
    public static void pop() {
        checkState(STACK.get() != null, "there is no active ScheduledContext");
        STACK.set(null);
    }
}
