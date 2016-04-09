package de.skuzzle.inject.async;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.skuzzle.inject.async.annotation.SimpleTrigger;

public class SimpleTriggerStrategy implements TriggerStrategy {

    @Inject
    private Injector injector;

    @Override
    public Class<SimpleTrigger> getTriggerType() {
        return SimpleTrigger.class;
    }

    @Override
    public void schedule(Method method, Object self, ScheduledExecutorService executor) {
        final SimpleTrigger trigger = method.getAnnotation(getTriggerType());
        checkArgument(trigger != null, "Method '%s' not annotated with @SimpleTrigger",
                method);

        final MethodInvocation invocation = InjectedMethodInvocation.forMethod(
                method, self, this.injector);
        final Runnable command = InvokeMethodRunnable.of(invocation);
        trigger.scheduleType()
                .schedule(executor,
                        command,
                        trigger.initialDelay(),
                        trigger.value(),
                        trigger.timeUnit());
    }

}
