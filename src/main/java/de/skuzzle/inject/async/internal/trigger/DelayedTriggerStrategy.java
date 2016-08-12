package de.skuzzle.inject.async.internal.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.DelayedTrigger;
import de.skuzzle.inject.async.internal.context.ContextFactory;
import de.skuzzle.inject.async.internal.runnables.LockableRunnable;
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
    @Inject
    private ContextFactory contextFactory;

    @Override
    public Class<DelayedTrigger> getTriggerType() {
        return DelayedTrigger.class;
    }

    @Override
    public ScheduledContext schedule(Method method, Object self,
            ScheduledExecutorService executor,
            ExceptionHandler handler) {
        final DelayedTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @DelayedTrigger",
                method);

        final InjectedMethodInvocation invocation = InjectedMethodInvocation
                .forMethod(method, self, this.injector);

        final ScheduledContext context = this.contextFactory.createContext(method);
        final LockableRunnable stack = this.runnableBuilder.createLockedRunnableStack(
                invocation, context, handler);

        try {
            final Future<?> future = executor.schedule(stack, trigger.value(),
                    trigger.timeUnit());
            context.setFuture(future);
        } finally {
            stack.release();
        }
        return context;
    }

}
