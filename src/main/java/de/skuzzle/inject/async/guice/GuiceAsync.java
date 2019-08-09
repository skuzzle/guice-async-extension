package de.skuzzle.inject.async.guice;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import de.skuzzle.inject.async.methods.AsyncModule;
import de.skuzzle.inject.async.methods.annotation.Async;
import de.skuzzle.inject.async.schedule.ScheduleModule;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;

/**
 * Entry point for enabling asynchronous method support within your guice application.
 *
 * <pre>
 * public class MyModule extends AbstractModule {
 *
 *     &#64;Override
 *     public void configure() {
 *         GuiceAsync.enableFor(binder());
 *         // ...
 *     }
 * }
 * </pre>
 *
 * Please see the JavaDoc of the {@link Async} and {@link Scheduled} annotation for
 * further usage information.
 *
 * @author Simon Taddiken
 * @see Async
 * @see Scheduled
 */
public final class GuiceAsync {

    private GuiceAsync() {
        // hidden constructor
    }

    /**
     * Enable support for the {@link Async} annotation in classes that are used with the
     * injector that will be created from the given {@link Binder}.
     *
     * @param binder The binder to register with.
     */
    public static void enableFor(Binder binder) {
        checkArgument(binder != null, "binder must not be null");
        binder.install(createModule());
    }

    /**
     * Creates a module that can be used to enable asynchronous method support.
     *
     * @return A module that exposes all bindings needed for asynchronous method support.
     * @since 0.2.0
     */
    public static Module createModule() {
        final GuiceAsync principal = new GuiceAsync();
        return new GuiceAsyncModule(principal);
    }

    private static final class GuiceAsyncModule extends AbstractModule {

        private final GuiceAsync principal;

        public GuiceAsyncModule(GuiceAsync principal) {
            this.principal = principal;
        }

        @Override
        protected void configure() {
            install(new AsyncModule(principal));
            install(new ScheduleModule(principal));
            bind(GuiceAsyncService.class).to(GuiceAsyncServiceImpl.class).in(Singleton.class);
        }

        @Provides
        @Singleton
        @DefaultBinding
        ThreadFactory provideThreadFactory() {
            return new ThreadFactoryBuilder()
                    .setNameFormat("guice-async-%d")
                    .build();
        }

        @Override
        public int hashCode() {
            return 31;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof GuiceAsyncModule;
        }
    }
}
