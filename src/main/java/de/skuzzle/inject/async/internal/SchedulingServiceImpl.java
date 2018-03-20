package de.skuzzle.inject.async.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Key;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.SchedulingService;
import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.Scheduled;

class SchedulingServiceImpl implements SchedulingService {

    private static final Logger LOG = LoggerFactory
            .getLogger(SchedulingServiceImpl.class);

    private final Provider<Injector> injector;
    private final Provider<TriggerStrategyRegistry> registry;

    SchedulingServiceImpl(Provider<Injector> injector,
            Provider<TriggerStrategyRegistry> registry) {
        this.injector = injector;
        this.registry = registry;
    }

    @Override
    public void scheduleMemberMethod(Method method, Object self) {
        scheduleMethod(method, self);
    }

    @Override
    public void scheduleStaticMethod(Method method) {
        scheduleMethod(method, null);
    }

    private void scheduleMethod(Method method, Object self) {
        if (!method.isAnnotationPresent(Scheduled.class)) {
            return;
        }
        final Annotation trigger = Annotations.findTriggerAnnotation(method);
        LOG.trace("Method '{}' is elligible for scheduling. Trigger is: {}", method,
                trigger);

        final Key<? extends ScheduledExecutorService> key = Keys.getSchedulerKey(
                method);
        final Key<? extends ExceptionHandler> handlerKey = Keys
                .getExceptionHandler(method);

        LOG.trace("Scheduler key is: {}, ExceptionHandler key is: {}", key,
                handlerKey);
        final ScheduledExecutorService scheduler = this.injector.get().getInstance(key);
        final ExceptionHandler handler = this.injector.get().getInstance(handlerKey);

        final TriggerStrategy strategy = this.registry.get().getStrategyFor(trigger);
        LOG.trace("Using trigger strategy: {}", strategy);
        strategy.schedule(method, self, scheduler, handler);
    }
}
