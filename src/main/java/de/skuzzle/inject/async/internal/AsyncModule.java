package de.skuzzle.inject.async.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.GuiceAsync;
import de.skuzzle.inject.async.annotation.Async;
import de.skuzzle.inject.async.internal.context.ContextInstaller;
import de.skuzzle.inject.async.internal.runnables.RunnablesInstaller;

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
        // do nothing
    }

    @Override
    protected void configure() {
        // the interceptor for @Async methods
        final MethodInterceptor asyncInterceptor = new AsynchronousMethodInterceptor();
        requestInjection(asyncInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Async.class),
                asyncInterceptor);

        ContextInstaller.install(binder());
        RunnablesInstaller.install(binder());

        bind(TriggerStrategyRegistry.class)
                .to(SpiTriggerStrategyRegistryImpl.class)
                .in(Singleton.class);
        bindListener(Matchers.any(), new SchedulerTypeListener());
        LOG.debug("Guice asynchronous method extension has been installed");
    }

    @Provides
    @Singleton
    @DefaultBinding
    ThreadFactory provideThreadFactory() {
        return new ThreadFactoryBuilder()
                .setNameFormat("guice-async-%d").build();
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
