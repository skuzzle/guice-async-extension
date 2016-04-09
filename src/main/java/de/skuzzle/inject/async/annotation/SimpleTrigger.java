package de.skuzzle.inject.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Trigger
public @interface SimpleTrigger {
    long value();
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    long initialDelay() default 0L;
    SimpleScheduleType scheduleType() default SimpleScheduleType.AT_FIXED_RATE;
}
