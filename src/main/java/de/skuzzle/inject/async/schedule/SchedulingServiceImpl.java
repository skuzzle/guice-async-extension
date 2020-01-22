package de.skuzzle.inject.async.schedule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Key;

import de.skuzzle.inject.async.guice.Keys;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;

class SchedulingServiceImpl implements SchedulingService {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulingServiceImpl.class);

    private final ScheduleProperties scheduleProperties;
    private final Provider<Injector> injector;
    private final Provider<TriggerStrategyRegistry> registry;
    private final Collection<ManuallyStarted> manuallyStarted;

    SchedulingServiceImpl(ScheduleProperties scheduleProperties, Provider<Injector> injector,
            Provider<TriggerStrategyRegistry> registry) {
        this.scheduleProperties = scheduleProperties;
        this.injector = injector;
        this.registry = registry;
        this.manuallyStarted = new ArrayList<>();
    }

    @Override
    public void scheduleMemberMethod(Method method, Object self) {
        scheduleMethod(method, self);
    }

    @Override
    public void scheduleStaticMethod(Method method) {
        scheduleMethod(method, null);
    }

    @Override
    public void startManualScheduling() {
        manuallyStarted.forEach(ManuallyStarted::scheduleNow);
        manuallyStarted.clear();
    }

    private void scheduleMethod(Method method, Object self) {
        if (!method.isAnnotationPresent(Scheduled.class)) {
            return;
        }
        final Annotation trigger = Annotations.findTriggerAnnotation(method);
        LOG.trace("Method '{}' is elligible for scheduling. Trigger is: {}", method, trigger);

        final Key<? extends ScheduledExecutorService> key = Keys.getSchedulerKey(method);
        final Key<? extends ExceptionHandler> handlerKey = Keys.getExceptionHandler(method);

        LOG.trace("Scheduler key is: {}, ExceptionHandler key is: {}", key, handlerKey);
        final ScheduledExecutorService scheduler = this.injector.get().getInstance(key);
        final ExceptionHandler handler = this.injector.get().getInstance(handlerKey);

        final TriggerStrategy strategy = this.registry.get().getStrategyFor(trigger);
        LOG.trace("Using trigger strategy: {}", strategy);

        final ScheduledContextImpl context = new ScheduledContextImpl(method, self);
        final InjectedMethodInvocation invocation = InjectedMethodInvocation.forMethod(method, self, injector.get());
        final LockableRunnable runnable = Runnables.createLockedRunnableStack(invocation, context, handler);

        if (mustStartManually(method)) {
            LOG.debug("Method '{}' is marked to be scheduled manually", method);
            this.manuallyStarted.add(new ManuallyStarted(context, scheduler, runnable, strategy));
        } else {
            strategy.schedule(context, scheduler, runnable);
        }
    }

    private boolean mustStartManually(Method method) {
        return !this.scheduleProperties.isAutoSchedulingEnabled() ||
                method.isAnnotationPresent(de.skuzzle.inject.async.schedule.annotation.ManuallyStarted.class);
    }

    private static class ManuallyStarted {
        private final ScheduledContext context;
        private final ScheduledExecutorService scheduler;
        private final LockableRunnable runnable;
        private final TriggerStrategy strategy;

        ManuallyStarted(ScheduledContext context, ScheduledExecutorService scheduler, LockableRunnable runnable,
                TriggerStrategy strategy) {
            this.context = context;
            this.scheduler = scheduler;
            this.runnable = runnable;
            this.strategy = strategy;
        }

        public void scheduleNow() {
            this.strategy.schedule(context, scheduler, runnable);
        }

    }

}
