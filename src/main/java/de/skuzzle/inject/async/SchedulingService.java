package de.skuzzle.inject.async;

import java.lang.reflect.Method;

import de.skuzzle.inject.async.annotation.Scheduled;

/**
 * Allows to manually schedule methods in the way they would normally automatically be
 * scheduled by the framework.
 *
 * @author Simon Taddiken
 * @since 0.4.0
 */
public interface SchedulingService {

    /**
     * Schedules the given member method if it is annotated with {@link Scheduled}. If it
     * is not, this method returns without performing any actions.
     *
     * @param method The method to schedule. Must be a non-static member method.
     * @param self The object to invoke the method on.
     */
    void scheduleMemberMethod(Method method, Object self);

    /**
     * Schedules the given static method if it is annotated with {@link Scheduled}. If it
     * is not, this method returns without performing any actions.
     *
     * @param method The method to schedule. Must be a static method.
     */
    void scheduleStaticMethod(Method method);
}
