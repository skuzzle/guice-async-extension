package de.skuzzle.inject.async.internal.trigger;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.skuzzle.inject.async.util.InjectedMethodInvocation;

@RunWith(MockitoJUnitRunner.class)
public class InvokeMethodRunnableTest {

    @Mock
    private InjectedMethodInvocation invocation;

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
