package de.skuzzle.inject.async;

import java.util.Map;

import de.skuzzle.inject.async.annotation.ExecutionScope;

/**
 * Holds contextual information for a single scheduled method execution.
 *
 * @author Simon Taddiken
 * @see ExecutionScope
 */
public interface ExecutionContext {

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