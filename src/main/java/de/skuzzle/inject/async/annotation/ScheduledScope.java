package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

import de.skuzzle.inject.async.ScheduledContext;

/**
 * A guice scope that pertains for one method annotated with {@link Scheduled}. Thus each
 * scheduled method has its own scope that persists over multiple executions of said
 * method.
 * <p>
 * Please not that this scope does only apply when a scheduled method is actually called
 * by the framework. If a scheduled method is called manually then this scope does not
 * apply.
 * </p>
 *
 * @author Simon
 * @see ScheduledContext
 * @since 0.3.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ScopeAnnotation
public @interface ScheduledScope {

}
