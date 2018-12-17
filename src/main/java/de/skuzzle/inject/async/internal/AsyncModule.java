package de.skuzzle.inject.async.internal;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeListener;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.GuiceAsync;
import de.skuzzle.inject.async.SchedulingService;
import de.skuzzle.inject.async.annotation.Async;

/**
 * Exposes required bindings. Use {@link GuiceAsync} to install this module for your own
 * environment.
 *
 * @author Simon Taddiken
 */
public final class AsyncModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncModule.class);

    /**
     * As {@link GuiceAsync} is not instantiatable from outside, the constructor guards
     * this class from being created unintentionally.
     *
     * @param principal The {@link GuiceAsync} instance that is installing this module.
     */
    public AsyncModule(GuiceAsync principal) {
        checkArgument(principal != null,
                "instantiating this module is not allowed. Use the class "
                        + "GuiceAsync to enable asynchronous method support.");
        // do nothing
    }

    public static boolean shutdownInternal(Injector injector, long timeout, TimeUnit timeUnit) {
        final ExecutorService executor = injector.getInstance(Keys.DEFAULT_EXECUTOR_KEY);
        boolean result = true;
        if (!shutdownExecutor(executor, timeout, timeUnit)) {
            LOG.warn("There are still active tasks lingering in default executor after shutdown. Wait time: {} {}",
                    timeout, timeUnit);
            result = false;
        }
        final ScheduledExecutorService scheduler = injector.getInstance(Keys.DEFAULT_SCHEDULER_KEY);
        if (!shutdownExecutor(scheduler, timeout, timeUnit)) {
            LOG.warn("There are still active tasks lingering in default scheduler after shutdown. Wait time: {} {}",
                    timeout, timeUnit);
            result = false;
        }
        return result;
    }

    private static boolean shutdownExecutor(ExecutorService executor, long timeout, TimeUnit timeUnit) {
        LOG.debug("Shutting down guice-async default executor instance {}", executor);
        executor.shutdownNow();

        try {
            return executor.awaitTermination(timeout, timeUnit);
        } catch (final InterruptedException e) {
            final Thread currentThread = Thread.currentThread();
            LOG.error("Thread {} interrupted while waiting to shutdown guice-async default executor",
                    currentThread.getName(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    protected void configure() {
        // the interceptor for @Async methods
        final MethodInterceptor asyncInterceptor = new AsynchronousMethodInterceptor();
        requestInjection(asyncInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Async.class),
                asyncInterceptor);

        bind(TriggerStrategyRegistry.class)
                .to(SpiTriggerStrategyRegistryImpl.class)
                .in(Singleton.class);

        final SchedulingService schedulingService = new SchedulingServiceImpl(
                getProvider(Injector.class),
                getProvider(TriggerStrategyRegistry.class));
        final TypeListener scheduleListener = new SchedulerTypeListener(
                schedulingService);

        bind(SchedulingService.class).toInstance(schedulingService);
        requestInjection(scheduleListener);
        bindListener(Matchers.any(), scheduleListener);
        LOG.debug("Guice asynchronous method extension has been installed");
    }

    @Provides
    @Singleton
    @DefaultBinding
    ThreadFactory provideThreadFactory() {
        return new ThreadFactoryBuilder()
                .setNameFormat("guice-async-%d")
                .build();
    }

    @Provides
    @Singleton
    @DefaultBinding
    ExecutorService provideDefaultExecutor(
            @DefaultBinding ThreadFactory threadFactory) {
        return Executors.newCachedThreadPool(threadFactory);
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
