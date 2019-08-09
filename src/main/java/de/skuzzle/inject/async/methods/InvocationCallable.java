package de.skuzzle.inject.async.methods;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.aopalliance.intercept.MethodInvocation;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;

/**
 * Wraps a {@link MethodInvocation} object in a {@link Callable} for asynchronous
 * execution. Besides Callable, the created class does also implement {@link Supplier}.
 * When calling the supplier's get() method, all non-runtime exceptions that occur during
 * execution will be wrapped into a {@link RuntimeException}.
 *
 * @author Simon Taddiken
 * @param <T> The return type of the method invocation.
 */
public class InvocationCallable<T> implements Callable<T>, Supplier<T> {

    private final MethodInvocation invocation;

    private InvocationCallable(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    /**
     * Wraps the given AOP method invocation into a runnable for asynchronous execution.
     * Note that there are some implicit preconditions regarding the wrapped invocation.
     *
     * @param invocation The invocation to wrap.
     * @return The callable to submit to an executor.
     */
    @SuppressWarnings("rawtypes")
    public static InvocationCallable<?> fromInvocation(MethodInvocation invocation) {
        return new InvocationCallable(invocation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T call() throws Exception {
        try {
            // As by the AsynchronousMethodInterceptor, the return type of the intercepted
            // method is either CompletableFuture, Future or void.
            final Object result = this.invocation.proceed();
            if (result instanceof Future<?>) {
                return (T) ((Future<?>) result).get();
            } else if (result != null) {
                throw new IllegalStateException(
                        "Wrapped invocation is expected to either " +
                                "return null or an instance of (Completable)Future");
            }
            return null;
        } catch (final Throwable e) {
            Throwables.throwIfInstanceOf(e, Exception.class);
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get() {
        try {
            return call();
        } catch (final Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("invocation", invocation)
                .toString();
    }
}
