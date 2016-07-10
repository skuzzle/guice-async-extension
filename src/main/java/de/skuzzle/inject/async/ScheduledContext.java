package de.skuzzle.inject.async;

import java.util.Map;

import de.skuzzle.inject.async.annotation.ExecutionScope;
import de.skuzzle.inject.async.annotation.ScheduledScope;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;

/**
 * Holds contextual information about a scheduled method. Any behavior is undefined when
 * methods on this object are called outside the scope of a scheduled method. Custom
 * {@link TriggerStrategy} implementations need to take care of supporting the scheduled
 * context theirself.
 *
 * @author Simon Taddiken
 * @see ScheduledScope
 * @see RunnableBuilder
 */
public interface ScheduledContext {

    /**
     * Records the start of a new execution performed in the current thread. Opens up a
     * new {@link ExecutionContext} which will be attached to the current thread until
     * {@link #finishExecution()} is called.
     *
     * @see ExecutionScope
     */
    void beginNewExecution();

    /**
     * Detaches the {@link ExecutionContext} from the current thread and thus closes the
     * current execution scope. Must be called from within the same thread from which
     * {@link #beginNewExecution()} has been called in order close the correct context.
     *
     * @see ExecutionScope
     */
    void finishExecution();

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
     * @return The number of times this method has been executed.
     */
    int getExecutionCount();

    /**
     * Gets the current execution context. This will yield a new object for every time the
     * method is scheduled again.
     *
     * @return The execution context.
     */
    ExecutionContext getExecution();

}