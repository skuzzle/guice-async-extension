package de.skuzzle.inject.async.internal.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.CronType;
import de.skuzzle.inject.async.internal.runnables.LockableRunnable;
import de.skuzzle.inject.async.internal.runnables.Reschedulable;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;

/**
 * TriggerStrategy that handles the {@link CronTrigger} annotation.
 *
 * @author Simon Taddiken
 */
public class CronTriggerStrategy implements TriggerStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(CronTriggerStrategy.class);

    @Inject
    private RunnableBuilder runnableBuilder;

    @Override
    public Class<CronTrigger> getTriggerType() {
        return CronTrigger.class;
    }

    @Override
    public void schedule(ScheduledContext context,
            ScheduledExecutorService executor,
            ExceptionHandler handler, LockableRunnable runnable) {

        final Method method = context.getMethod();
        final CronTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @CronTrigger",
                method);

        LOG.debug("Initially scheduling method '{}' on '{}' with trigger: {}", method, context.getSelf(), trigger);
        final CronType cronType = trigger.cronType();
        final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(cronType.getType());
        final CronParser parser = new CronParser(cronDefinition);
        final Cron cron = parser.parse(trigger.value());
        final ExecutionTime execTime = ExecutionTime.forCron(cron);

        final Reschedulable rescheduleRunnable = this.runnableBuilder.reschedule(
                context,
                runnable.release(),
                executor,
                execTime);

        rescheduleRunnable.scheduleNextExecution();
    }
}
