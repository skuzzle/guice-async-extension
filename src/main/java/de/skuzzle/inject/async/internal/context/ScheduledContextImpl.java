package de.skuzzle.inject.async.internal.context;

import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.inject.async.ExecutionContext;
import de.skuzzle.inject.async.ScheduledContext;

public class ScheduledContextImpl implements ScheduledContext {

    private final Object mutex;
    private final Map<String, Object> beanMap;
    private final ThreadLocal<ExecutionContextImpl> execution;
    private volatile int executionCount;
    private volatile boolean stopRequested;

    public ScheduledContextImpl() {
        this.mutex = new Object();
        this.beanMap = new HashMap<>();
        this.execution = new ThreadLocal<>();
    }

    public void beginNewExecution() {
        final ExecutionContextImpl executionContext;
        synchronized (mutex) {
            executionContext = new ExecutionContextImpl(executionCount);
        }
        this.execution.set(executionContext);
        ScheduledContextHolder.push(this);
    }

    public void finishExecution() {
        ScheduledContextHolder.pop();
        final ExecutionContext activeContext = execution.get();
        checkState(activeContext != null, "there is no active ExecutionContext");
        this.execution.set(null);
        synchronized (this.mutex) {
            ++this.executionCount;
        }
    }

    @Override
    public void requestStop() {
        this.stopRequested = true;
    }

    @Override
    public boolean isStopRequested() {
        return stopRequested;
    }

    @Override
    public int getExecutionCount() {
        return this.executionCount;
    }

    @Override
    public ExecutionContextImpl getExecution() {
        final ExecutionContextImpl activeContext = execution.get();
        checkState(activeContext != null, "there is no active ExecutionContext");
        return activeContext;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.beanMap;
    }
}
