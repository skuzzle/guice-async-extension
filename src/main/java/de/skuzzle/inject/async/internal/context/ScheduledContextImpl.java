package de.skuzzle.inject.async.internal.context;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.skuzzle.inject.async.ExecutionContext;
import de.skuzzle.inject.async.ScheduledContext;

class ScheduledContextImpl implements ScheduledContext {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledContextImpl.class);

    private final Object mutex;
    private final Method method;
    private final Object self;
    private final Map<String, Object> beanMap;
    private final ThreadLocal<ExecutionContextImpl> execution;
    private volatile int executionCount;
    private volatile Future<?> future;

    public ScheduledContextImpl(Method method, Object self) {
        this.method = method;
        this.self = self;
        this.mutex = new Object();
        this.beanMap = new HashMap<>();
        this.execution = new ThreadLocal<>();
    }

    @Override
    public void setFuture(Future<?> future) {
        checkArgument(future != null, "future must not be null");
        this.future = future;
    }

    private void checkFutureSet() {
        checkState(this.future != null, "setFuture has not been called. "
                + "There might be something wrong with the TriggerStrategy"
                + " implementation.");
    }

    @Override
    public void cancel(boolean mayInterrupt) {
        checkFutureSet();
        LOG.debug("Cancel called on ScheduledContext: {}", this);
        this.future.cancel(mayInterrupt);
    }

    @Override
    public boolean isCancelled() {
        checkFutureSet();
        return this.future.isCancelled();
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object getSelf() {
        return this.self;
    }

    @Override
    public void beginNewExecution() {
        final ExecutionContextImpl executionContext;
        synchronized (this.mutex) {
            executionContext = new ExecutionContextImpl(this.method,
                    this.executionCount++);
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
        return MoreObjects.toStringHelper(this)
                .add("executionCount", executionCount)
                .add("method", method)
                .add("self", self)
                .add("properties", beanMap)
                .toString();
    }
}
