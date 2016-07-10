package de.skuzzle.inject.async;

import java.util.Map;

import de.skuzzle.inject.async.annotation.ScheduledScope;

/**
 * Holds contextual information about a scheduled method.
 * 
 * @author Simon Taddiken
 * @see ScheduledScope
 */
public interface ScheduledContext {
    
    /**
     * Gets the properties that are attached to this context.
     * 
     * @return The context properties.
     */
    Map<String, Object> getProperties();

    /**
     * Requests to never schedule the method to which this context belongs again. This
     * does not affect any currently running executions. This does also not interrupt any
     * thread nor does it shut down the executor. Whether a stop has been requested can be
     * queried with {@link #isStopRequested()}.
     */
    void requestStop();

    /**
     * Whether stopping to reschedule has been requested using {@link #requestStop()}.
     * 
     * @return Whether to stop rescheduling the method to which this context belongs.
     * @see #requestStop()
     */
    boolean isStopRequested();

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