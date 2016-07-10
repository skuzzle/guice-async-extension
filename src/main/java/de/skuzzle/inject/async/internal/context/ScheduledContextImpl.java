package de.skuzzle.inject.async.internal.context;

import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.inject.async.ExecutionContext;
import de.skuzzle.inject.async.ScheduledContext;

class ScheduledContextImpl implements ScheduledContext {

    private final Object mutex;
    private final Map<String, Object> beanMap;
    private final ThreadLocal<ExecutionContextImpl> execution;
    private volatile int executionCount;

    public ScheduledContextImpl() {
        this.mutex = new Object();
        this.beanMap = new HashMap<>();
        this.execution = new ThreadLocal<>();
    }

    @Override
    public void beginNewExecution() {
        final ExecutionContextImpl executionContext;
        synchronized (this.mutex) {
            executionContext = new ExecutionContextImpl(this.executionCount);
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
}
