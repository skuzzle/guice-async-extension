package de.skuzzle.inject.async;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
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

        final GuiceAsync principal = new GuiceAsync();
        binder.install(new AsyncModule(principal));
        binder.install(new ContextModule(principal));
        binder.install(new RunnablesModule(principal));
    }

    /**
     * Creates a module that can be used to enable asynchronous method support.
     *
     * @return A module that exposes all bindings needed for asynchronous method support.
     * @since 0.2.0
     */
    public static Module createModule() {
        final GuiceAsync principal = new GuiceAsync();
        return new AbstractModule() {

            @Override
            protected void configure() {
                install(new AsyncModule(principal));
                install(new ContextModule(principal));
                install(new RunnablesModule(principal));
            }
        };
    }

}
