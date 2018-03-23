package de.skuzzle.inject.async;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;

public class GuiceAsyncTest {

    @Test
    public void testEnable() throws Exception {
        final Binder binder = mock(Binder.class);
        GuiceAsync.enableFor(binder);
        verify(binder).install(GuiceAsync.createModule());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullBinder() throws Exception {
        GuiceAsync.enableFor(null);
    }

    @Test
    public void testCreateModule() throws Exception {
        Guice.createInjector(GuiceAsync.createModule());
    }
}
