package de.skuzzle.inject.async;

import static com.google.common.base.Preconditions.checkArgument;

import org.aopalliance.intercept.MethodInvocation;

import com.google.common.base.Throwables;

class InvokeMethodRunnable implements Runnable {

    private final MethodInvocation invocation;

    private InvokeMethodRunnable(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    static Runnable of(MethodInvocation invocation) {
        checkArgument(invocation != null);
        return new InvokeMethodRunnable(invocation);
    }

    @Override
    public void run() {
        try {
            this.invocation.proceed();
        } catch (final Throwable e) {
            Throwables.propagate(e);
        }
    }
}
