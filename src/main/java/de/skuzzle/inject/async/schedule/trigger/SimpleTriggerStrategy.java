package de.skuzzle.inject.async.schedule.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import de.skuzzle.inject.async.schedule.ExceptionHandler;
import de.skuzzle.inject.async.schedule.LockableRunnable;
import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.schedule.TriggerStrategy;
import de.skuzzle.inject.async.schedule.annotation.SimpleTrigger;

/**
 * TriggerStrategy that handles the {@link SimpleTrigger} annotation for defining simple
 * periodic executions.
 *
 * @author Simon Taddiken
 */
public class SimpleTriggerStrategy implements TriggerStrategy {

    @Override
    public Class<SimpleTrigger> getTriggerType() {
        return SimpleTrigger.class;
    }

    @Override
    public void schedule(ScheduledContext context, ScheduledExecutorService executor,
            ExceptionHandler handler, LockableRunnable runnable) {

        final Method method = context.getMethod();
        final SimpleTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @SimpleTrigger",
                method);

        try {
            final Future<?> future = trigger.scheduleType().schedule(
                    executor,
                    runnable,
                    trigger.initialDelay(),
                    trigger.value(),
                    trigger.timeUnit());
            context.setFuture(future);
        } finally {
            runnable.release();
        }
    }

}
