package de.skuzzle.inject.async.internal.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.MoreObjects;

import de.skuzzle.inject.async.ExecutionContext;

/**
 * Holds contextual information about a single scheduled method invocation.
 *
 * @author Simon Taddiken
 */
class ExecutionContextImpl implements ExecutionContext {

    private final Map<String, Object> properties = new HashMap<>();
    private final int executionNr;
    private final Method method;

    ExecutionContextImpl(Method method, int executionNr) {
        this.method = method;
        this.executionNr = executionNr;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public int getExecutionNr() {
        return this.executionNr;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("method", method)
                .add("executionNr", executionNr)
                .add("properties", properties)
                .toString();
    }
}
