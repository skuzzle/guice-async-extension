package de.skuzzle.inject.async;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Entry point for enabling asynchronous method support within your guice
 * application.
 *
 * <pre>
 *
 * public class MyModule extends AbstractModule {
 *
 * &#64;Override public void configure() { GuiceAsync.enableFor(binder()); //
 * ... } }
 *
 * <pre>
 *
 * Please see the JavaDoc of the {@link Async} annotation for further usage
 * information.
 *
 * @author Simon Taddiken
 * @see Async
 */
public final class GuiceAsync {

    private GuiceAsync() {
        // hidden constructor
    }

    /**
     * Enable support for the {@link Async} annotation in classes that are used
     * with the injector that will be created from the given {@link Binder}.
     *
     * @param binder The binder to register with.
     */
    public static void enableFor(Binder binder) {
        checkArgument(binder != null, "binder must not be null");
        final Module module = new AsyncModule();
        binder.install(module);
    }

}
