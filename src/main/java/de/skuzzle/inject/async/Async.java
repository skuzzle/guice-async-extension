package de.skuzzle.inject.async;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Tags a method to be run asynchronously.
 *
 * @author Simon Taddiken
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface Async {

}
