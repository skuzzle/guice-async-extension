package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.skuzzle.inject.async.schedule.TriggerStrategy;

/**
 * Marks another annotation type to be a <em>trigger annotation</em>. A single trigger
 * annotation must be put on a method additionally to the {@link Scheduled}
 * annotation. It holds meta information about how the method will be scheduled.
 * For any trigger annotation, there must be a {@link TriggerStrategy} which is
 * able to interpret the meta information provided by the annotation.
 *
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Trigger {

}
