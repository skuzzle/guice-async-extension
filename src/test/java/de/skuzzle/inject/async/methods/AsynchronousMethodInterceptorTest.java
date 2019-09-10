package de.skuzzle.inject.async.methods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Key;

@RunWith(MockitoJUnitRunner.class)
public class AsynchronousMethodInterceptorTest {

    @Mock
    private Injector injector;
    @InjectMocks
    private AsynchronousMethodInterceptor subject;

    @Mock
    private Key<? extends ExecutorService> key;
    @Mock
    private ExecutorService executorService;
    @Mock
    private Future<String> future;

    @Before
    public void setUp() throws Exception {
        when(this.injector.getInstance(Mockito.any(Key.class))).thenReturn(this.executorService);
        when(this.executorService.submit(Mockito.any(Callable.class))).thenReturn(this.future);

        when(this.future.get()).thenReturn("result");
    }

    private MethodInvocation mockInvocation(String methodName) throws Exception {
        final Method method = getClass().getMethod(methodName);
        final MethodInvocation invocation = mock(MethodInvocation.class);
        final Object[] args = new Object[0];
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getArguments()).thenReturn(args);
        return invocation;
    }

    public String methodWithReturnType() {
        return "";
    }

    public void voidMethod() {

    }

    public Void voidWrapperMethod() {
        return null;
    }

    public Future<String> futureMethod() {
        return this.future;
    }

    @Test(expected = RuntimeException.class)
    public void testDeclineReturnType() throws Throwable {
        final MethodInvocation invocation = mockInvocation("methodWithReturnType");
        this.subject.invoke(invocation);
    }

    @Test
    public void testInvokeVoidMethod() throws Throwable {
        final MethodInvocation invocation = mockInvocation("voidMethod");
        final Object result = this.subject.invoke(invocation);
        assertNull(result);
        verify(this.executorService).submit(Mockito.any(Callable.class));
    }

    @Test
    public void testInvokeVoidWrapper() throws Throwable {
        final MethodInvocation invocation = mockInvocation("voidWrapperMethod");
        final Object result = this.subject.invoke(invocation);
        assertNull(result);
        verify(this.executorService).submit(Mockito.any(Callable.class));
    }

    @Test
    public void testInvokeFuture() throws Throwable {
        final MethodInvocation invocation = mockInvocation("futureMethod");
        final Future<String> result = (Future<String>) this.subject.invoke(invocation);
        assertEquals("result", result.get());
    }

}
