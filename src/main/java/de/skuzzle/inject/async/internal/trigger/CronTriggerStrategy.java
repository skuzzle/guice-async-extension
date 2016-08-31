package de.skuzzle.inject.async.internal.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.inject.Injector;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.CronType;
import de.skuzzle.inject.async.internal.context.ContextFactory;
import de.skuzzle.inject.async.internal.runnables.Reschedulable;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

/**
 * TriggerStrategy that handles the {@link CronTrigger} annotation.
 *
 * @author Simon Taddiken
 */
public class CronTriggerStrategy implements TriggerStrategy {

    @Inject
    private Injector injector;
    @Inject
    private RunnableBuilder runnableBuilder;
    @Inject
    private ContextFactory contextFactory;

    @Override
    public Class<CronTrigger> getTriggerType() {
        return CronTrigger.class;
    }

    @Override
    public ScheduledContext schedule(Method method, Object self,
            ScheduledExecutorService executor,
            ExceptionHandler handler) {
        final CronTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @CronTrigger",
                method);

        final CronType cronType = trigger.cronType();
        final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(
                cronType.getType());
        final CronParser parser = new CronParser(cronDefinition);
        final Cron cron = parser.parse(trigger.value());
        final ExecutionTime execTime = ExecutionTime.forCron(cron);

        final InjectedMethodInvocation invocation = InjectedMethodInvocation
                .forMethod(method, self, this.injector);

        final ScheduledContext context = this.contextFactory.createContext(method);
        final Runnable runnable = this.runnableBuilder.createRunnableStack(invocation,
                context, handler);
        final Reschedulable rescheduleRunnable = this.runnableBuilder.reschedule(
                context,
                runnable,
                executor,
                execTime);

        rescheduleRunnable.scheduleNextExecution();
        return context;
    }
}
