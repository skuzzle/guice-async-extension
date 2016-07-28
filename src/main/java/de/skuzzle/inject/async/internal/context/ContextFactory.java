package de.skuzzle.inject.async.internal.context;

import java.lang.reflect.Method;

import de.skuzzle.inject.async.ScheduledContext;

/**
 * Used to create {@link ScheduledContext} objects for scheduled methods.
 *
 * @author Simon Taddiken
 */
public interface ContextFactory {

    /**
     * Creates a new {@link ScheduledContext} instance which can be used to manage a
     * single scheduled method.
     *
     * @param method the method being scheduled for execution.
     * @return The context.
     */
    ScheduledContext createContext(Method method);
}
