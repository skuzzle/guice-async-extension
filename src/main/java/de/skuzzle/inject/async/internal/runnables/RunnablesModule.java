package de.skuzzle.inject.async.internal.runnables;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;

import de.skuzzle.inject.async.GuiceAsync;

/**
 * Purely used internal to install runnables related bindings into the main module.
 *
 * @author Simon Taddiken
 */
public final class RunnablesModule extends AbstractModule {

    public RunnablesModule(GuiceAsync principal) {
        checkArgument(principal != null,
                "instantiating this module is not allowed. Use the class "
                        + "GuiceAsync to enable asynchronous method support.");
    }

    @VisibleForTesting
    RunnablesModule() {
    }

    @Override
    protected void configure() {
        bind(RunnableBuilder.class).to(RunnableBuilderImpl.class).asEagerSingleton();
    }
}
