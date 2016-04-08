package de.skuzzle.inject.async;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import de.skuzzle.inject.async.annotation.Trigger;

/**
 * Defines how a certain {@link Trigger} annotation is handled in order to
 * extract scheduling meta information and to actually schedule method
 * invocations.
 *
 * @author Simon Taddiken
 */
public interface TriggerStrategy {

    /**
     * Returns the annotation type that this strategy can handle.
     *
     * @return The annotation type.
     */
    Class<? extends Annotation> getTriggerType();

    /**
     * Extracts scheduling information from the provided {@link Method} and then
     * schedules invocations of that method according to the information.
     *
     * <p>
     * To support invocation of parameterized methods, implementors can refer to
     * {@link InjectedMethodInvocation} to inject actual parameters of a method.
     * </p>
     *
     * @param method The method to schedule.
     * @param self The object to invoke the method on.
     * @param executor The executor to use for scheduling.
     */
    void schedule(Method method, Object self, ScheduledExecutorService executor);
}
