package de.skuzzle.inject.async.internal;

import java.lang.annotation.Annotation;

import de.skuzzle.inject.async.TriggerStrategy;

/**
 * Holds registered {@link TriggerStrategy} implementations. The way in which
 * strategies are registered with a registry is not defined and is thus
 * implementation specific.
 *
 * @author Simon Taddiken
 */
public interface TriggerStrategyRegistry {

    /**
     * Gets the {@link TriggerStrategy} that is able to handle the provided
     * trigger annotation. If there is no appropriate strategy, an exception is
     * thrown.
     *
     * @param triggerAnnotation The trigger annotation.
     * @return The {@link TriggerStrategy}
     * @see TriggerStrategy#getTriggerType()
     */
    TriggerStrategy getStrategyFor(Annotation triggerAnnotation);
}
