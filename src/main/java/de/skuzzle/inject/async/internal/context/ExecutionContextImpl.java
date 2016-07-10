package de.skuzzle.inject.async.internal.context;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.inject.async.ExecutionContext;

/**
 * Holds contextual information about a single scheduled method invocation.
 * 
 * @author Simon Taddiken
 */
class ExecutionContextImpl implements ExecutionContext {

    private final Map<String, Object> beanMap = new HashMap<>();
    private final int executionNr;

    ExecutionContextImpl(int executionNr) {
        this.executionNr = executionNr;
    }

    @Override
    public int getExecutionNr() {
        return this.executionNr;
    }

    /**
     * Returns the map that stores the beans in the scope of this execution.
     * 
     * @return The bean map.
     */
    Map<String, Object> getBeanMap() {
        return this.beanMap;
    }
}
