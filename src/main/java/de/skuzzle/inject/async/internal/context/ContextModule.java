package de.skuzzle.inject.async.internal.context;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;

import de.skuzzle.inject.async.ExecutionContext;
import de.skuzzle.inject.async.GuiceAsync;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.annotation.ExecutionScope;
import de.skuzzle.inject.async.annotation.ScheduledScope;
import de.skuzzle.inject.async.util.MapBasedScope;
import de.skuzzle.inject.proxy.ScopedProxyBinder;

/**
 * Purely used internal to install context related bindings into the main module.
 *
 * @author Simon Taddiken
 */
public final class ContextModule extends AbstractModule {

    public ContextModule(GuiceAsync principal) {
        checkArgument(principal != null,
                "instantiating this module is not allowed. Use the class "
                        + "GuiceAsync to enable asynchronous method support.");
    }

    @VisibleForTesting
    ContextModule() {
    }

    @Override
    protected void configure() {
        bind(ContextFactory.class).to(ContextFactoryImpl.class).asEagerSingleton();

        final Provider<Map<String, Object>> executionMap = () -> ScheduledContextHolder
                .getContext().getExecution().getProperties();

        bindScope(ExecutionScope.class, MapBasedScope.withMapSupplier(executionMap));

        final Provider<Map<String, Object>> scheduledMap = () -> ScheduledContextHolder
                .getContext().getProperties();
        bindScope(ScheduledScope.class, MapBasedScope.withMapSupplier(scheduledMap));

        ScopedProxyBinder.using(binder())
                .bind(ScheduledContext.class)
                .toProvider(ScheduledContextHolder::getContext);

        ScopedProxyBinder.using(binder())
                .bind(ExecutionContext.class)
                .toProvider(
                        () -> ScheduledContextHolder.getContext().getExecution());
    }

}
