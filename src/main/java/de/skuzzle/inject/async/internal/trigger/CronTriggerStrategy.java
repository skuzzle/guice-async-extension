package de.skuzzle.inject.async.internal.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.ServiceLoader;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.inject.Injector;

import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.internal.TriggerStrategyRegistry;
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

    private final CronDefinition cronDefinition;

    /**
     * Public constructor for being instantiated by the {@link ServiceLoader}. Supports
     * being used with the default {@link TriggerStrategyRegistry} which performs member
     * injection on all strategies that are loaded using the ServiceLoader.
     */
    public CronTriggerStrategy() {
        this.cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(
                CronType.QUARTZ);
    }

    @Override
    public Class<CronTrigger> getTriggerType() {
        return CronTrigger.class;
    }

    @Override
    public void schedule(Method method, Object self, ScheduledExecutorService executor) {
        final CronTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @CronTrigger",
                method);

        final CronParser parser = new CronParser(this.cronDefinition);
        final Cron cron = parser.parse(trigger.value());
        final ExecutionTime execTime = ExecutionTime.forCron(cron);

        final InjectedMethodInvocation invocation = InjectedMethodInvocation
                .forMethod(method, self, this.injector);

        final ScheduledContext context = this.contextFactory.createContext();
        final Runnable invokeRunnable = this.runnableBuilder.invoke(invocation);
        final Runnable scopedRunnable = this.runnableBuilder.scope(invokeRunnable, context);
        final Reschedulable rescheduleRunnable = this.runnableBuilder.reschedule(
                scopedRunnable,
                executor,
                execTime);

        rescheduleRunnable.scheduleNextExecution();
    }
}
