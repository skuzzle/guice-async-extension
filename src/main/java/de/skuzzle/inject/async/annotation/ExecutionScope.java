package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

import de.skuzzle.inject.async.schedule.ExecutionContext;

/**
 * A guice scope that pertains for a single execution of a scheduled method. You can use
 * execution scoped classes e.g. for injecting a new object as parameter to a scheduled
 * method each time it is called. This is a sub scope of {@link ScheduledScope}. It does
 * not persist objects over multiple method calls.
 * <p>
 * Please not that this scope does only apply when a scheduled method is actually called
 * by the framework. If a scheduled method is called manually then this scope does not
 * apply.
 * </p>
 *
 * @author Simon
 * @see ExecutionContext
 * @since 0.3.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ScopeAnnotation
public @interface ExecutionScope {

}
