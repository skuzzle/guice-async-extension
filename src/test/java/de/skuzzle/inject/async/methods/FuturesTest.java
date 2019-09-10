package de.skuzzle.inject.async.methods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.methods.Futures;

public class FuturesTest {

    @Test
    public void tesstFutureContract() throws Exception {
        final Future<String> future = Futures.delegate("result");
        assertFalse(future.cancel(true));
        assertFalse(future.isCancelled());
        assertTrue(future.isDone());
        assertEquals("result", future.get());
        assertEquals("result", future.get(Long.MAX_VALUE, TimeUnit.DAYS));
    }

    @Test
    public void testPrivateCtor() throws Exception {
        final Constructor<GuiceAsync> ctor = GuiceAsync.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
}
