package de.skuzzle.inject.async.schedule;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.CircularDependencyProxy;

import de.skuzzle.inject.async.schedule.MapBasedScope;

@RunWith(MockitoJUnitRunner.class)
public class MapBasedScopeTest {

    private Map<String, Object> scopeMap;

    private Scope subject;

    @Before
    public void setup() {
        this.scopeMap = new HashMap<>();
        final Provider<Map<String, Object>> provider = () -> this.scopeMap;
        this.subject = MapBasedScope.withMapSupplier(provider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNull() throws Exception {
        MapBasedScope.withMapSupplier(null);
    }

    @Test
    public void testScopeNull() throws Exception {
        final Provider<String> unscoped = () -> null;

        final Provider<String> scoped = this.subject.scope(Key.get(String.class),
                unscoped);

        assertNull(scoped.get());
        assertNull(scoped.get());
    }

    @Test
    public void testScopeNonNull() throws Exception {
        final Provider<String> unscoped = () -> new String("foo");

        final Provider<String> scoped = this.subject.scope(Key.get(String.class),
                unscoped);

        final String first = scoped.get();
        final String second = scoped.get();
        assertSame(first, second);
    }

    @Test
    public void testDontScopeProxies() throws Exception {
        final Provider<CircularDependencyProxy> unscoped = () -> mock(
                CircularDependencyProxy.class);

        final Provider<CircularDependencyProxy> scoped = this.subject.scope(
                Key.get(CircularDependencyProxy.class), unscoped);

        final CircularDependencyProxy first = scoped.get();
        final CircularDependencyProxy second = scoped.get();

        assertNotSame(first, second);
    }
}
