package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Injector;

/**
 * A trigger annotation that allows to define a simple periodicity for method
 * execution.
 *
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Trigger
public @interface SimpleTrigger {
    /**
     * The period (in case of fixed rate scheduling) resp. the delay (in case of
     * fixed delay scheduling). By default, this value is interpreted as
     * milliseconds. You can change this by additionally supplying the
     * {@link #timeUnit()} field.
     *
     * @return The period/delay that will be used for scheduling.
     */
    long value();

    /**
     * The time unit in which the {@link #value()} and {@link #initialDelay()}
     * fields are interpreted. Defaults to milliseconds.
     *
     * @return The time unit.
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * The initial delay to wait before first execution of the annotated method.
     * the delay applies to the time at which a type containing a scheduled
     * method is constructed by the {@link Injector}. By default, this value is
     * interpreted as milliseconds. You can change this by additionally
     * supplying the {@link #timeUnit()} field.
     *
     * @return The initial delay.
     */
    long initialDelay() default 0L;

    /**
     * Defines the way in which the execution is scheduled at a
     * {@link ScheduledExecutorService} implementation.
     *
     * @return The scheduling type.
     */
    SimpleScheduleType scheduleType() default SimpleScheduleType.AT_FIXED_RATE;
}
