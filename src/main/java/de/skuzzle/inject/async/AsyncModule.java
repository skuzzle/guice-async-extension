package de.skuzzle.inject.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;

class AsyncModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ExecutorKeyService.class).in(Singleton.class);
        final MethodInterceptor asyncInterceptor = new AsynchronousMethodInterceptor();
        requestInjection(asyncInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Async.class),
                asyncInterceptor);
    }

    @Provides
    @DefaultExecutor
    ThreadFactory provideThreadFactory() {
        return new ThreadFactoryBuilder()
                .setNameFormat("guice-async-%d").build();
    }

    @Provides
    @DefaultExecutor
    ExecutorService provideDefaultExecutor(
            @DefaultExecutor ThreadFactory threadFactory) {
        return Executors.newCachedThreadPool(threadFactory);
    }

}
