package de.skuzzle.inject.async.schedule;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeListener;

import de.skuzzle.inject.async.guice.DefaultBinding;
import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.schedule.annotation.ExecutionScope;
import de.skuzzle.inject.async.schedule.annotation.ScheduledScope;
import de.skuzzle.inject.proxy.ScopedProxyBinder;

/**
 * Purely used internal to install context related bindings into the main module.
 *
 * @author Simon Taddiken
 */
public final class ScheduleModule extends AbstractModule {

    /**
     * This constructor is only allowed to be called from within the {@link GuiceAsync}
     * class.
     *
     * @param principal Restricts construction, not allowed to be null.
     */
    public ScheduleModule(GuiceAsync principal) {
        checkArgument(principal != null,
                "instantiating this module is not allowed. Use the class "
                        + "GuiceAsync to enable asynchronous method support.");
    }

    @Override
    protected void configure() {
        bind(TriggerStrategyRegistry.class)
                .to(SpiTriggerStrategyRegistryImpl.class)
                .in(Singleton.class);

        final SchedulingService schedulingService = new SchedulingServiceImpl(
                getProvider(Injector.class),
                getProvider(TriggerStrategyRegistry.class));

        final TypeListener scheduleListener = new SchedulerTypeListener(schedulingService);
        bind(SchedulingService.class).toInstance(schedulingService);
        requestInjection(scheduleListener);
        bindListener(Matchers.any(), scheduleListener);

        final Provider<Map<String, Object>> executionMap = () -> ScheduledContextHolder
                .getContext().getExecution().getProperties();

        bindScope(ExecutionScope.class, MapBasedScope.withMapSupplier(executionMap));

        final Provider<Map<String, Object>> scheduledMap = () -> ScheduledContextHolder
                .getContext().getProperties();
        bindScope(ScheduledScope.class, MapBasedScope.withMapSupplier(scheduledMap));

        ScopedProxyBinder.using(binder())
                .bind(ScheduledContext.class)
                .toProvider(ScheduledContextHolder::getContext);

        ScopedProxyBinder.using(binder())
                .bind(ExecutionContext.class)
                .toProvider(
                        () -> ScheduledContextHolder.getContext().getExecution());
    }

    @Provides
    @Singleton
    @DefaultBinding
    ScheduledExecutorService provideScheduler(
            @DefaultBinding ThreadFactory threadFactory) {
        final int cores = Runtime.getRuntime().availableProcessors();
        return Executors.newScheduledThreadPool(cores, threadFactory);
    }

    @Provides
    @Singleton
    @DefaultBinding
    ExceptionHandler provideDefaultExceptionHandler() {
        return new DefaultExceptionHandler();
    }
}
