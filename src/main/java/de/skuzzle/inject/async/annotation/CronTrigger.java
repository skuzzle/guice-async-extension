package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cronutils.model.definition.CronDefinition;

/**
 * A trigger annotation for specifying a cron pattern which describes the
 * scheduling of the method.
 *
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Trigger
public @interface CronTrigger {
	/**
	 * Specifies the cron pattern to use. The actual format depends on the used
	 * {@link CronDefinition}.
	 *
	 * @return The cron pattern.
	 */
	String value();

	/**
	 * Specifies the format of the cron pattern used.
	 * 
	 * @return The cron type.
	 */
	CronType cronType() default CronType.QUARTZ;
}
