package de.skuzzle.inject.async.internal;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Injector;
import com.google.inject.Key;

class AsynchronousMethodInterceptor implements MethodInterceptor {

    @Inject
    private Injector injector;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Method method = invocation.getMethod();
        checkReturnType(method.getReturnType());

        final Key<? extends ExecutorService> key = Keys.getExecutorKey(method);
        final ExecutorService executor = this.injector.getInstance(key);
        final Future<?> future = executor.submit(InvocationCallable
                .fromInvocation(invocation));

        if (method.getReturnType() == Void.class || method.getReturnType() == Void.TYPE) {
            return null;
        }
        return future;
    }

    private static void checkReturnType(Class<?> returnType) {
        if (returnType != Void.class && returnType != Void.TYPE
                && !Future.class.isAssignableFrom(returnType)) {
            throw new IllegalArgumentException(
                    "Methods annotated with @Async must either return void or a Future");
        }
    }

}
