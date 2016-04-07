package de.skuzzle.inject.async;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import org.aopalliance.intercept.MethodInvocation;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.inject.Injector;

import de.skuzzle.inject.async.annotation.CronTrigger;

public class CronTriggerStrategy implements TriggerStrategy {

    private final Injector injector;
    private final CronDefinition cronDefinition;
    private final CronParser cronParser;

    public CronTriggerStrategy(Injector injector) {
        this.injector = injector;

        this.cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(
                CronType.QUARTZ);
        this.cronParser = new CronParser(this.cronDefinition);
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
        final Cron cron = this.cronParser.parse(trigger.value());
        final ExecutionTime execTime = ExecutionTime.forCron(cron);
        final MethodInvocation invocation = InjectedMethodInvocation.forMethod(method,
                self, this.injector);
        final Runnable action = new ReScheduleRunnable(invocation, executor, execTime);
        executor.execute(action);
    }
}
