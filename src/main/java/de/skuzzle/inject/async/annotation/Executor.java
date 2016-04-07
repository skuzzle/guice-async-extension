package de.skuzzle.inject.async.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;

import com.google.inject.Key;

/**
 * Allows the specify the class part of the {@link Key} that is used to look up
 * an {@link ExecutorService} for a method annotated with {@link Async}. Please
 * refer to the {@link Async} documentation for further information.
 *
 * @author Simon Taddiken
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Executor {
    /**
     * Specifies the type of the executor to use.
     *
     * @return The executor type.
     */
    Class<? extends ExecutorService> value();
}
