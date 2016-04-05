package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvocationCallableTest {

    @Mock
    private MethodInvocation invocation;

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testProceed() throws Throwable {
        final String expected = "result";
        when(this.invocation.proceed()).thenReturn(Futures.delegate(expected));
        final Callable<?> callable = InvocationCallable.fromInvocation(this.invocation);
        final Object result = callable.call();

        assertEquals(expected, result);
    }

    @Test(expected = RuntimeException.class)
    public void testThrowable() throws Throwable {
        doThrow(Throwable.class).when(this.invocation).proceed();
        final Callable<?> callable = InvocationCallable.fromInvocation(this.invocation);
        callable.call();
    }

    @Test(expected = Exception.class)
    public void testException() throws Throwable {
        doThrow(Exception.class).when(this.invocation).proceed();
        final Callable<?> callable = InvocationCallable.fromInvocation(this.invocation);
        callable.call();
    }

    @Test
    public void testPrivateCtor() throws Exception {
        final Constructor<GuiceAsync> ctor = GuiceAsync.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
}
