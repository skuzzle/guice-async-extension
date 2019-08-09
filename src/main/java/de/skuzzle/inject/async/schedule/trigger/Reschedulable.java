package de.skuzzle.inject.async.schedule.trigger;

/**
 * Runnable extension that allows to reschedule itself.
 *
 * @author Simon Taddiken
 */
public interface Reschedulable extends Runnable {

    /**
     * Schedules the next execution of this runnable. The way in which the runnable is
     * actually scheduled is implementation dependent.
     */
    void scheduleNextExecution();
}
