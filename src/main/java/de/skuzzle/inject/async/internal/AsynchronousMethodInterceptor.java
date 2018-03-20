package de.skuzzle.inject.async.internal;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Injector;
import com.google.inject.Key;

import de.skuzzle.inject.async.internal.runnables.InvocationCallable;

class AsynchronousMethodInterceptor implements MethodInterceptor {

    @Inject
    private Injector injector;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Method method = invocation.getMethod();
        checkReturnType(method.getReturnType());

        final Key<? extends ExecutorService> key = Keys.getExecutorKey(method);
        final ExecutorService ex = this.injector.getInstance(key);

        final InvocationCallable<?> callable = InvocationCallable
                .fromInvocation(invocation);
        if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
            return CompletableFuture.supplyAsync(callable, ex);
        } else {
            final Future<?> future = ex.submit(callable);
            if (isVoid(method.getReturnType())) {
                return null;
            }
            return future;
        }
    }

    private static boolean isVoid(Class<?> type) {
        return type == Void.class || type == Void.TYPE;
    }

    private static void checkReturnType(Class<?> returnType) {
        if (!(isVoid(returnType) || Future.class.isAssignableFrom(returnType))) {
            throw new IllegalArgumentException(
                    "Methods annotated with @Async must either return void or (Completable)Future");
        }
    }

}
