package de.skuzzle.inject.async;

import de.skuzzle.inject.async.annotation.ExecutionScope;

/**
 * Holds contextual information for a single scheduled method execution.
 * 
 * @author Simon Taddiken
 * @see ExecutionScope
 */
public interface ExecutionContext {

    /**
     * Returns the execution number. This number is incremented each time a scheduled
     * method is scheduled again. The first execution is denoted with 0, the second with 1
     * and so on.
     * 
     * @return The execution nr.
     */
    int getExecutionNr();

}