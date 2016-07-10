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

/**
 * Purely used internal to install context related bindings into the main module.
 * 
 * @author Simon Taddiken
 */
public final class ContextInstaller {

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
            final Provider<Map<String, Object>> executionMap = 
                    () -> ScheduledContextHolder.getContext().getExecution().getBeanMap();
            bindScope(ExecutionScope.class, MapBasedScope.withMapSupplier(executionMap));
            
            final Provider<Map<String, Object>> scheduledMap = 
                    () -> ScheduledContextHolder.getContext().getProperties();
            bindScope(ScheduledScope.class, MapBasedScope.withMapSupplier(scheduledMap));
            
            bind(ScheduledContext.class).toProvider(ScheduledContextHolder::getContext);
            bind(ExecutionContext.class).toProvider(
                    () -> ScheduledContextHolder.getContext().getExecution());
        }
        
    }
}
