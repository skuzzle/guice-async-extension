package de.skuzzle.inject.async.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.util.MethodVisitor;

class SchedulerTypeListener implements TypeListener {

    private static final Logger LOG = LoggerFactory.getLogger(
            SchedulerTypeListener.class);

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        final Provider<Injector> injector = encounter.getProvider(Injector.class);
        final Provider<TriggerStrategyRegistry> registry = encounter.getProvider(
                TriggerStrategyRegistry.class);

        encounter.register(new InjectionListener<I>() {

            @Override
            public void afterInjection(I injectee) {
                final Consumer<Method> action = getMethodProcessor(injectee,
                        injector, registry);
                MethodVisitor.forEachMemberMethod(injectee.getClass(), action);
            }
        });
    }

    private static Consumer<Method> getMethodProcessor(Object self, Provider<Injector> injector,
            Provider<TriggerStrategyRegistry> registry) {

        return method -> {
            if (!method.isAnnotationPresent(Scheduled.class)) {
                return;
            }
            final Annotation trigger = Annotations.findTriggerAnnotation(method);
            LOG.trace("Method '{}' is eligible for scheduling. Trigger is: {}", method,
                    trigger);

            final Key<? extends ScheduledExecutorService> key = Keys.getSchedulerKey(
                    method);
            LOG.trace("Scheduler key is: {}", key);
            final ScheduledExecutorService scheduler = injector.get().getInstance(key);
            LOG.trace("Using scheduler '{}'", scheduler);
            final TriggerStrategy strategy = registry.get().getStrategyFor(trigger);
            LOG.trace("Using trigger strategy: {}", strategy);
            strategy.schedule(method, self, scheduler);
        };
    }

}
