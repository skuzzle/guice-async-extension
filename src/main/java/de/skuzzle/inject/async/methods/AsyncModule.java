package de.skuzzle.inject.async.methods;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;

import de.skuzzle.inject.async.guice.DefaultBinding;
import de.skuzzle.inject.async.guice.GuiceAsync;

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

    @Override
    protected void configure() {
        // the interceptor for @Async methods
        final MethodInterceptor asyncInterceptor = new AsynchronousMethodInterceptor();
        requestInjection(asyncInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Async.class),
                asyncInterceptor);

        LOG.debug("Guice asynchronous method extension has been installed");
    }

    @Provides
    @Singleton
    @DefaultBinding
    ExecutorService provideDefaultExecutor(
            @DefaultBinding ThreadFactory threadFactory) {
        return Executors.newCachedThreadPool(threadFactory);
    }

}
