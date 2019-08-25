package de.skuzzle.inject.async.schedule;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.skuzzle.inject.async.schedule.annotation.ExecutionScope;

class ScheduledContextImpl implements ScheduledContext {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledContextImpl.class);

    // guards the creation of a new ExecutionContext
    private final Object executionContextLock;
    private final Method method;
    private final Object self;
    private final Map<String, Object> beanMap;
    private final ThreadLocal<ExecutionContextImpl> execution;
    private volatile int executionCount;

    private final Object futureLock;
    private volatile Future<?> future;

    public ScheduledContextImpl(Method method, Object self) {
        this.method = method;
        this.self = self;
        this.executionContextLock = new Object();
        this.futureLock = new Object();
        this.beanMap = new HashMap<>();
        this.execution = new ThreadLocal<>();
    }

    @Override
    public void cancel(boolean mayInterrupt) {
        synchronized (futureLock) {
            checkFutureSet();
            LOG.debug("Cancel called on ScheduledContext: {}", this);
            this.future.cancel(mayInterrupt);
        }
    }

    @Override
    public boolean isCancelled() {
        synchronized (futureLock) {
            checkFutureSet();
            return this.future.isCancelled();
        }
    }

    private void setFuture(Future<?> future) {
        checkArgument(future != null, "future must not be null");
        this.future = future;
    }

    @Override
    public void updateFuture(Supplier<Future<?>> futureSupplier) {
        checkArgument(futureSupplier != null, "futureSupplier must not be null");
        synchronized (futureLock) {
            setFuture(futureSupplier.get());
        }
    }

    private void checkFutureSet() {
        checkState(this.future != null, "setFuture has not been called. "
                + "There might be something wrong with the TriggerStrategy"
                + " implementation.");
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object getSelf() {
        return this.self;
    }

    /**
     * Records the start of a new execution performed in the current thread. Opens up a
     * new {@link ExecutionContext} which will be attached to the current thread until
     * {@link #finishExecution()} is called.
     *
     * @see ExecutionScope
     */
    void beginNewExecution() {
        final ExecutionContextImpl executionContext;
        synchronized (this.executionContextLock) {
            executionContext = new ExecutionContextImpl(this.method,
                    this.executionCount++);
        }
        this.execution.set(executionContext);
        ScheduledContextHolder.push(this);
    }

    /**
     * Detaches the {@link ExecutionContext} from the current thread and thus closes the
     * current execution scope. Must be called from within the same thread from which
     * {@link #beginNewExecution()} has been called in order to close the correct context.
     *
     * @see ExecutionScope
     */
    void finishExecution() {
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
