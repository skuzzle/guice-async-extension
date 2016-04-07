package de.skuzzle.inject.async;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvokeMethodRunnableTest {

    @Mock
    private MethodInvocation invocation;

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testRun() throws Throwable {
        final Runnable r = InvokeMethodRunnable.of(this.invocation);
        r.run();
        verify(this.invocation).proceed();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfNull() throws Exception {
        InvokeMethodRunnable.of(null);
    }

    @Test(expected = RuntimeException.class)
    public void testRunThrowsRuntimeException() throws Throwable {
        doThrow(RuntimeException.class).when(this.invocation).proceed();
        final Runnable r = InvokeMethodRunnable.of(this.invocation);
        r.run();
    }

    @Test(expected = Error.class)
    public void testRunThrowError() throws Throwable {
        doThrow(Error.class).when(this.invocation).proceed();
        final Runnable r = InvokeMethodRunnable.of(this.invocation);
        r.run();
    }
}
