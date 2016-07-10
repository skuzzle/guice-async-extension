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
    

    public static ScheduledContextImpl getContext() {
        final ScheduledContextImpl activeContext = STACK.get();
        checkState(activeContext != null, 
                "Scope 'ScheduledScope' is currently not active");
        return activeContext;
    }
    
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
    
    public static void pop() {
        checkState(STACK.get() != null, "there is no active ScheduledContext");
        STACK.set(null);
    }
}
