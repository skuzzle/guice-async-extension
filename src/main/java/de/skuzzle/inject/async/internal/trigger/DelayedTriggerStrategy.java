package de.skuzzle.inject.async.internal.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.DelayedTrigger;
import de.skuzzle.inject.async.internal.context.ScheduledContextImpl;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

/**
 * Handles the {@link DelayedTrigger}.
 *
 * @author Simon Taddiken
 * @since 0.2.0
 */
public class DelayedTriggerStrategy implements TriggerStrategy {

    @Inject
    private Injector injector;
    @Inject
    private RunnableBuilder runnableBuilder;

    @Override
    public Class<DelayedTrigger> getTriggerType() {
        return DelayedTrigger.class;
    }

    @Override
    public void schedule(Method method, Object self, ScheduledExecutorService executor) {
        final DelayedTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @DelayedTrigger",
                method);

        final InjectedMethodInvocation invocation = InjectedMethodInvocation
                .forMethod(method, self, this.injector);

        final ScheduledContextImpl context = new ScheduledContextImpl();
        final Runnable invokeRunnable = runnableBuilder.invoke(invocation);
        final Runnable scopedRunnable = runnableBuilder.scope(invokeRunnable, context);
        executor.schedule(scopedRunnable, trigger.value(), trigger.timeUnit());
    }

}
