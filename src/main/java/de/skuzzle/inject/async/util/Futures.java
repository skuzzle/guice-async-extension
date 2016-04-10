package de.skuzzle.inject.async.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;

import de.skuzzle.inject.async.annotation.Async;

/**
 * Utility to created dummy {@link Future} objects to use in methods annotated
 * with {@link Async}.
 *
 * @author Simon Taddiken
 */
public final class Futures {

    private Futures() {
        // hidden ctor
    }

    /**
     * Returns a dummy {@link Future} object to be used to return a value in
     * methods that are annotated with {@link Async}. Sample usage:
     *
     * <pre>
     * &#64;Async
     * public Future&lt;Integer&gt; calculateAsync() {
     *     final Integer result = doHeavyCalculation();
     *     return Futures.delegate(result);
     * }
     * </pre>
     *
     * You should not use the returned dummy object for any other purpose than
     * returning it from a method which is annotated with {@link Async}.
     *
     * <p>
     * <b>API Note</b><br>
     * The {@link MethodInterceptor} which handles the asynchronous invocations
     * will extract the wrapped Object from this dummy Future and create an
     * actual Future object by submitting the method invocation to an
     * {@link ExecutorService}. Thus, the dummy Object created here will never
     * leave the context of the method it is created in (if you do not manually
     * leak it to the outside).
     * </p>
     *
     * @param <T> Type of the Object to return.
     * @param obj The Object to return.
     * @return The dummy object.
     * @see Async
     */
    public static <T> Future<T> delegate(T obj) {
        return new Future<T>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public T get() {
                return obj;
            }

            @Override
            public T get(long timeout, TimeUnit unit) {
                return get();
            }
        };
    }
}
