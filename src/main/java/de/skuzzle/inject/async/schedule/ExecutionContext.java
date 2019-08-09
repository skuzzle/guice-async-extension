package de.skuzzle.inject.async.schedule;

import java.lang.reflect.Method;
import java.util.Map;

import de.skuzzle.inject.async.annotation.ExecutionScope;

/**
 * Holds contextual information for a single scheduled method execution.
 *
 * <p>
 * An instance of this class is available for injection. It is bound as scoped proxy and
 * can thus also be injected into singletons. However, calling methods on the proxy'ed
 * context will only work if the context is active for the current thread.
 * </p>
 *
 * @author Simon Taddiken
 * @see ExecutionScope
 * @since 0.3.0
 */
public interface ExecutionContext {

    /**
     * Gets the scheduled method to which this scope belongs.
     *
     * @return The method.
     */
    Method getMethod();

    /**
     * Gets the properties that are attached to this context. This map is also used by the
     * {@link ExecutionScope} implementation to store cached objects.
     *
     * @return The context properties.
     */
    Map<String, Object> getProperties();

    /**
     * Returns the execution number. This number is incremented each time a scheduled
     * method is scheduled again. The first execution is denoted with 0, the second with 1
     * and so on.
     *
     * @return The execution nr.
     */
    int getExecutionNr();

}