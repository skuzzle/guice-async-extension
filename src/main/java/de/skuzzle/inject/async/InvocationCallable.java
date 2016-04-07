package de.skuzzle.inject.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.aopalliance.intercept.MethodInvocation;

import com.google.common.base.Throwables;

/**
 * Wraps a {@link MethodInvocation} object in a {@link Callable} for
 * asynchronous execution.
 *
 * @author Simon Taddiken
 * @param <T> The return type of the method invocation.
 */
public class InvocationCallable<T> implements Callable<T> {

    private final MethodInvocation invocation;

    private InvocationCallable(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    @SuppressWarnings("rawtypes")
    public static Callable<?> fromInvocation(MethodInvocation invocation) {
        return new InvocationCallable(invocation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T call() throws Exception {
        try {
            // As by the AsynchronousMethodInterceptor, the return type of the intercepted
            // method is either a Future or void.
            final Object result = this.invocation.proceed();
            if (result instanceof Future<?>) {
                return (T) ((Future<?>) result).get();
            } else if (result != null) {
                throw new IllegalStateException(
                        "Wrapped invocation is expected to either " +
                        "return null or an instance of Future");
            }
            return null;
        } catch (final Throwable e) {
            Throwables.propagateIfInstanceOf(e, Exception.class);
            throw Throwables.propagate(e);
        }
    }

}
