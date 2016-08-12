package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Provider;

import de.skuzzle.inject.async.ExceptionHandler;

/**
 * Can be put on a method which is annotated with {@link Scheduled} to additionally
 * specify an exception handler. Please note that the exception handler instance is
 * obtained from the injector in early stages, <em>before</em> initially scheduling the
 * method. If you intend to inject {@link ScheduledScope} or {@link ExecutionScope}
 * objects into the exception handler, you need to bind them as
 * <a href="https://github.com/skuzzle/guice-scoped-proxy-extension">scoped proxy</a> or
 * inject a {@link Provider}
 *
 * @author Simon Taddiken
 * @see Scheduled
 * @since 0.3.0
 * @see ExceptionHandler
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
