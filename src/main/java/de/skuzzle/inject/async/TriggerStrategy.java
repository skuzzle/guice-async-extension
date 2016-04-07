package de.skuzzle.inject.async;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

public interface TriggerStrategy {

    Class<? extends Annotation> getTriggerType();

    void schedule(Method method, Object self, ScheduledExecutorService executor);
}
