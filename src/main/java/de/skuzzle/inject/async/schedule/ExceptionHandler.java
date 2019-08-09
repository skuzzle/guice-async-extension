package de.skuzzle.inject.async.schedule;

import de.skuzzle.inject.async.annotation.OnError;
import de.skuzzle.inject.async.annotation.Scheduled;

/**
 * May be used to react to errors thrown from {@link Scheduled scheduled} methods. You can
 * specify the {@link ExceptionHandler} to use for scheduling by putting the
 * {@link OnError} annotation on the scheduled method.
 *
 * @author Simon Taddiken
 * @since 0.3.0
 * @see OnError
 */
public interface ExceptionHandler {

    /**
     * Call back for handling exceptions that occur in scheduled methods.
     *
     * @param e The exception.
     */
    void onException(Exception e);
}
