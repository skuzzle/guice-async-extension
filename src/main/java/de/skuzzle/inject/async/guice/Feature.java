package de.skuzzle.inject.async.guice;

import de.skuzzle.inject.async.methods.annotation.Async;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;

/**
 * Supported features.
 *
 * @author Simon Taddiken
 */
public enum Feature {
    /** This feature enables handling of the {@link Async} annotation. */
    ASYNC,
    /** This feature enables handling of the {@link Scheduled} annotation. */
    SCHEDULE
}
