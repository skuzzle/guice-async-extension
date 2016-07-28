package de.skuzzle.inject.async.internal.context;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.inject.async.ExecutionContext;
import de.skuzzle.inject.async.ScheduledContext;

class ScheduledContextImpl implements ScheduledContext {

    private final Object mutex;
    private final Method method;
    private final Map<String, Object> beanMap;
    private final ThreadLocal<ExecutionContextImpl> execution;
    private volatile int executionCount;

    public ScheduledContextImpl(Method method) {
        this.method = method;
        this.mutex = new Object();
        this.beanMap = new HashMap<>();
        this.execution = new ThreadLocal<>();
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void beginNewExecution() {
        final ExecutionContextImpl executionContext;
        synchronized (this.mutex) {
            executionContext = new ExecutionContextImpl(this.method, this.executionCount);
        }
        this.execution.set(executionContext);
        ScheduledContextHolder.push(this);
    }

    @Override
    public void finishExecution() {
        ScheduledContextHolder.pop();
        final ExecutionContext activeContext = this.execution.get();
        checkState(activeContext != null, "there is no active ExecutionContext");
        this.execution.set(null);
        synchronized (this.mutex) {
            ++this.executionCount;
        }
    }

    @Override
    public int getExecutionCount() {
        return this.executionCount;
    }

    @Override
    public ExecutionContextImpl getExecution() {
        final ExecutionContextImpl activeContext = this.execution.get();
        checkState(activeContext != null, "there is no active ExecutionContext");
        return activeContext;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.beanMap;
    }

    @Override
    public String toString() {
        return String.format("ScheduledContext[method: %s]", this.method.getName());
    }
}
