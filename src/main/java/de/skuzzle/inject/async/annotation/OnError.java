package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ExecutionContext;
import de.skuzzle.inject.async.ScheduledContext;

/**
 * Can be put on a method which is annotated with {@link Scheduled} to additionally
 * specify an exception handler. Please note that the exception handler instance is
 * obtained from the injector in early stages, before scheduling the method. Thus it
 * should either be bound as singleton or as
 * <a href="https://github.com/skuzzle/guice-scoped-proxy-extension">scoped proxy</a> in
 * case you need to inject the current {@link ScheduledContext} or
 * {@link ExecutionContext} into the handler.
 *
 * @author Simon Taddiken
 * @see Scheduled
 * @since 0.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnError {
    /**
     * The type of the {@link ExceptionHandler} to use for the scheduled method.
     *
     * @return The type.
     */
    Class<? extends ExceptionHandler> value();

}
