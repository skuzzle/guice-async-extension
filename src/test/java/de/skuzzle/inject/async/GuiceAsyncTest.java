package de.skuzzle.inject.async;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
}
