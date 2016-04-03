package de.skuzzle.inject.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.aopalliance.intercept.MethodInterceptor;

public final class Futures {

    /**
     * Returns a dummy {@link Future} object to be used to return a value in
     * methods that are annotated with {@link Async}. Sample usage:
     *
     * <pre>
     * &#64;Async
     * public Future&lt;Integer&gt; calculateAsync() {
     *     final Integer result = doHeavyCalculation();
     *     return Futures.wrap(result);
     * }
     * </pre>
     *
     * The returned object only supports the {@link Future#get()} method which
     * will return the object that has been passed as argument. All other
     * methods will throw an exception.
     *
     * @param <T> Type of the Object to return.
     * @param obj The Object to return.
     * @return The dummy object.
     * @apiNote The {@link MethodInterceptor} which handles the asynchronous
     *          invocations will extract the wrapped Object from this dummy
     *          Future and create an actual Future object by submitting the
     *          method invocation to an {@link ExecutorService}.
     */
    public static <T> Future<T> delegate(T obj) {
        return new Future<T>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isCancelled() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isDone() {
                throw new UnsupportedOperationException();
            }

            @Override
            public T get() {
                return obj;
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException,
                    ExecutionException, TimeoutException {
                throw new UnsupportedOperationException();
            }
        };
    }

    private Futures() {
        // hidden ctor
    }
}
