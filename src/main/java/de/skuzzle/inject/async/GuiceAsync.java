package de.skuzzle.inject.async;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.TimeUnit;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.skuzzle.inject.async.annotation.Async;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.internal.AsyncModule;
import de.skuzzle.inject.async.internal.context.ContextModule;
import de.skuzzle.inject.async.internal.runnables.RunnablesModule;

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
     * Performs an orderly shutdown of the internally used thread pools, using a best
     * effort approach for stopping all active tasks. When supplied a timeout > 0 the
     * current thread will block until all threads have terminated or the timeout has been
     * reached. As the framework uses two internal executors, the timeout will be waited
     * for twice!
     *
     * @param injector The injector for which guice-async extension has been
     *            {@link #enableFor(Binder) enabled for}.
     * @param timeout The time that the threads are given to terminate.
     * @param timeUnit The unit of above timeout value.
     * @return <code>true</code> if all threads terminated within the provided timeout,
     *         <code>false</code> if not or if the current thread has been interrupted
     *         while waiting for the timeout.
     * @since 1.2.0
     */
    public static boolean shutdown(Injector injector, long timeout, TimeUnit timeUnit) {
        checkArgument(injector != null, "injector must not be null");
        return AsyncModule.shutdownInternal(injector, timeout, timeUnit);
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
            install(new ContextModule(principal));
            install(new RunnablesModule(principal));
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
