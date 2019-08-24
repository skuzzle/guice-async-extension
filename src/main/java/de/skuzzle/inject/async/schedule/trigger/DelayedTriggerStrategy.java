package de.skuzzle.inject.async.schedule.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import de.skuzzle.inject.async.schedule.LockableRunnable;
import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.schedule.TriggerStrategy;
import de.skuzzle.inject.async.schedule.annotation.DelayedTrigger;

/**
 * Handles the {@link DelayedTrigger}.
 *
 * @author Simon Taddiken
 * @since 0.2.0
 */
public class DelayedTriggerStrategy implements TriggerStrategy {

    @Override
    public Class<DelayedTrigger> getTriggerType() {
        return DelayedTrigger.class;
    }

    @Override
    public void schedule(ScheduledContext context, ScheduledExecutorService executor, LockableRunnable runnable) {
        final Method method = context.getMethod();

        final DelayedTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @DelayedTrigger",
                method);

        try {
            final Future<?> future = executor.schedule(runnable, trigger.value(),
                    trigger.timeUnit());
            context.setFuture(future);
        } finally {
            runnable.release();
        }
    }

}
