package de.skuzzle.inject.async;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Binder;

public class GuiceAsyncTest {

    @Test
    public void testEnable() throws Exception {
        final Binder binder = mock(Binder.class);
        GuiceAsync.enableFor(binder);
        verify(binder).install(Mockito.isA(AsyncModule.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullBinder() throws Exception {
        GuiceAsync.enableFor(null);
    }

    @Test
    public void testPrivateCtor() throws Exception {
        final Constructor<GuiceAsync> ctor = GuiceAsync.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
}
