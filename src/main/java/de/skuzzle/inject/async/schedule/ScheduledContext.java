package de.skuzzle.inject.async.schedule;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import de.skuzzle.inject.async.schedule.annotation.ScheduledScope;

/**
 * Holds contextual information about a scheduled method. Any behavior is undefined when
 * methods on this object are called outside the scope of a scheduled method. Custom
 * {@link TriggerStrategy} implementations need to take care of supporting the scheduled
 * context theirself.
 *
 * <p>
 * An instance of this class is available for injection. It is bound as scoped proxy and
 * can thus also be injected into singletons. However, calling methods on the proxy'ed
 * context will only work if the context is active for the current thread.
 * </p>
 *
 * @author Simon Taddiken
 * @see ScheduledScope
 * @since 0.3.0
 */
public interface ScheduledContext {

    /**
     * Gets the scheduled method to which this scope belongs.
     *
     * @return The method.
     */
    Method getMethod();

    /**
     * Returns the object on which this method will be invoked. Returns null in case this
     * context belongs to a static method.
     *
     * @return The object.
     */
    Object getSelf();

    /**
     * Gets the properties that are attached to this context. This map is also used by the
     * {@link ScheduledScope} implementation to store cached objects.
     *
     * @return The context properties.
     */
    Map<String, Object> getProperties();

    /**
     * Gets the number of times that this method has been executed. Note: the value might
     * be out of date by the time it is returned. You might want to use
     * {@link ExecutionContext#getExecutionNr()} to figure out the number of the current
     * execution.
     *
     * <p>
     * Since version 1.1.0 this number no longer denotes the number of times that the
     * method had <em>finished</em> executing. It now returns the number of times that the
     * method has been called, disregarding whether all calls have already returned.
     * </p>
     *
     * @return The number of times this method has been called.
     */
    int getExecutionCount();

    /**
     * Gets the current execution context. This will yield a new object for every time the
     * method is scheduled again.
     *
     * @return The execution context.
     */
    ExecutionContext getExecution();

    /**
     * Cancels this scheduled method. The method will never be scheduled again on the
     * current instance after this method has been called. Current executions will be
     * interrupted if the flag is passed.
     * <p>
     * In case the scheduled method is not contained in a singleton scoped object it
     * <b>will</b> be scheduled again once another instance of the object has been
     * created. If the scheduled method was a static one, it will never be scheduled
     * again.
     * </p>
     *
     * @param mayInterrupt Whether running executions may be interrupted.
     * @since 0.4.0
     */
    void cancel(boolean mayInterrupt);

    /**
     * Atomically updates the Future object that represents the current scheduling. The
     * future will be obtained by invoking the given supplier. This method should only be
     * called by {@link TriggerStrategy} implementations to make the context support the
     * {@link #cancel(boolean)} method. If no Future object is set by the strategy,
     * canceling will not be possible.
     *
     * @param futureSupplier Supplies the future instance.
     */
    void updateFuture(Supplier<Future<?>> futureSupplier);

    /**
     * Returns true if cancel has been called on this context.
     *
     * @return Whether this scheduled method has been cancelled.
     * @since 0.4.0
     */
    boolean isCancelled();
}