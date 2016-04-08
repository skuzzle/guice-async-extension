package de.skuzzle.inject.async;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import javax.inject.Provider;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import de.skuzzle.inject.async.annotation.Scheduled;

class SchedulerTypeListener implements TypeListener {

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

    @VisibleForTesting
    Consumer<Method> getMethodProcessor(Object self, Provider<Injector> injector,
            Provider<TriggerStrategyRegistry> registry) {

        return method -> {
            if (!method.isAnnotationPresent(Scheduled.class)) {
                return;
            }
            final Annotation trigger = Annotations.findTriggerAnnotation(method);
            final Key<? extends ScheduledExecutorService> key =
                    Keys.getSchedulerKey(method);
            final ScheduledExecutorService scheduler = injector.get().getInstance(key);
            final TriggerStrategy strategy = registry.get().getStrategyFor(trigger);
            strategy.schedule(method, self, scheduler);
        };
    }

}
