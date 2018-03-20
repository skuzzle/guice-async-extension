package de.skuzzle.inject.async.internal.context;

import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provider;

import de.skuzzle.inject.async.ExecutionContext;
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
public final class ContextInstaller {

    private ContextInstaller() {
        // hidden
    }

    /**
     * Install context scopes for given binder.
     *
     * @param binder The binder.
     */
    public static void install(Binder binder) {
        binder.install(new ContextModule());
    }

    private static final class ContextModule extends AbstractModule {

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

}
