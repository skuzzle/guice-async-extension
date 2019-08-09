package de.skuzzle.inject.async.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * A trigger that will execute a method only once after a certain delay. The
 * delay is measured from the time at which an object that contains the
 * annotated method is newly constructed by the Injector.
 *
 * @author Simon Taddiken
 * @since 0.2.0
 */
@Trigger
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DelayedTrigger {
    /**
     * The delay after which the method will be executed. By default this value
     * is interpreted as being milliseconds. You can change the default time
     * unit by specifying the {@link #timeUnit()} parameter.
     *
     * @return The delay.
     */
    long value();

    /**
     * The time unit in which the {@link #value() delay} will be interpreted. Defaults to
     * milliseconds.
     *
     * @return The time unit.
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
