package de.skuzzle.inject.async.schedule;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;

/**
 * A map-supplier based scope implementation. The scope stores objects in a map which is
 * supplied by a {@link Provider} which is specified at creation time of the scope. Each
 * time a scoped object is requested, the provider is queried to access the current scope
 * map. By returning different map instances, different scopes can be distinguished.
 *
 * @author Simon Taddiken
 */
public class MapBasedScope implements Scope {

    private enum NullObject {
        INSTANCE
    }

    private final Provider<Map<String, Object>> mapProvider;

    private MapBasedScope(Provider<Map<String, Object>> mapProvider) {
        this.mapProvider = mapProvider;
    }

    /**
     * Creates a new scope which stores all instances into the map provided by the passed
     * provider. The actual scope is thus determined by the map instance returned by the
     * provider.
     *
     * @param mapProvider Provider which supplies the map for storing scoped instances.
     * @return The scope.
     */
    public static Scope withMapSupplier(Provider<Map<String, Object>> mapProvider) {
        checkArgument(mapProvider != null, "mapProvider is null");
        return new MapBasedScope(mapProvider);
    }

    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        final String name = key.toString();
        return () -> {
            final Map<String, Object> beans = this.mapProvider.get();

            synchronized (beans) {
                final Object bean = beans.get(name);
                if (bean == NullObject.INSTANCE) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                T t = (T) bean;
                if (t == null) {
                    t = unscoped.get();
                    if (!Scopes.isCircularProxy(t)) {
                        beans.put(name, t == null ? NullObject.INSTANCE : t);
                    }
                }
                return t;
            }
        };
    }
}
