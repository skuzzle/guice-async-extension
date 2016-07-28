package de.skuzzle.inject.async;

/**
 *
 * @author Simon Taddiken
 * @since 0.3.0
 */
public interface ExceptionHandler {

    void onException(Exception exception);
}
