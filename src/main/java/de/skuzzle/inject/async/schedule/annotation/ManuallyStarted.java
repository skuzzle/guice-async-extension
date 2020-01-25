package de.skuzzle.inject.async.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.skuzzle.inject.async.schedule.SchedulingService;

/**
 * Disables auto-scheduling of the annotated method. By default, if a method is annotated
 * with {@link Scheduled}, it will be scheduled by the time its type/instance is
 * encountered/created by the Guice framework. Using this annotation you can defer the
 * actual scheduling until {@link SchedulingService#startManualScheduling()} is called.
 *
 * @author Simon Taddiken
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ManuallyStarted {}
