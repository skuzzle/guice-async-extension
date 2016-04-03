package de.skuzzle.inject.async;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;

@Target(METHOD)
@Retention(RUNTIME)
public @interface Executor {
    Class<? extends ExecutorService> value();
}
