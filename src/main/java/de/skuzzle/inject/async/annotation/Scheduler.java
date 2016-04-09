package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.Key;

/**
 * Allows to specify the class part of the {@link Key} that is used to look up a
 * {@link ScheduledExecutorService} for a method annotated with
 * {@link Scheduled}. Please refer to the {@link Scheduled} documentation for
 * further information.
 *
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Scheduler {
    /**
     * Specifies the type of the scheduler to use.
     *
     * @return The scheduler type.
     */
    Class<? extends ScheduledExecutorService> value();
}
