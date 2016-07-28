package de.skuzzle.inject.async.internal.runnables;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;

/**
 * Purely used internal to install runnables related bindings into the main module.
 *
 * @author Simon Taddiken
 */
public final class RunnablesInstaller {

    /**
     * Installs some bindings to the given binder.
     *
     * @param binder The binder.
     */
    public static void install(Binder binder) {
        binder.install(new RunnablesModule());
    }

    private RunnablesInstaller() {
        // hidden
    }

    private static final class RunnablesModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(RunnableBuilder.class).to(RunnableBuilderImpl.class).asEagerSingleton();
        }

    }
}
