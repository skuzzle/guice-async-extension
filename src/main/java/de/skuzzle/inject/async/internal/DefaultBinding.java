package de.skuzzle.inject.async.internal;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;

import com.google.inject.BindingAnnotation;

/**
 * Internal binding annotation for binding a default {@link ExecutorService} as
 * fall back.
 *
 * @author Simon Taddiken
 */
@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
@interface DefaultBinding {

}
