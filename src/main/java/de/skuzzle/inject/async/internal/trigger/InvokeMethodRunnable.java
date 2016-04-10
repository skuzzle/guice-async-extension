package de.skuzzle.inject.async.internal.trigger;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ScheduledExecutorService;

import org.aopalliance.intercept.MethodInvocation;

import com.google.common.base.Throwables;

/**
 * Wraps a {@link MethodInvocation} in a {@link Runnable} to allow it to be
 * scheduled with a {@link ScheduledExecutorService}. When the resulting
 * runnable is executed, it will invoke {@link MethodInvocation#proceed()} and
 * discard any return value. All exceptions will be propagated by wrapping them
 * in a {@link RuntimeException}.
 *
 * @author Simon Taddiken
 */
class InvokeMethodRunnable implements Runnable {

    private final MethodInvocation invocation;

    private InvokeMethodRunnable(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    /**
     * Creates a Runnable which will proceed the given MethodInvocation when
     * being executed.
     *
     * @param invocation the invocation to call.
     * @return The runnable.
     */
    public static Runnable of(MethodInvocation invocation) {
        checkArgument(invocation != null);
        return new InvokeMethodRunnable(invocation);
    }

    @Override
    public void run() {
        try {
            this.invocation.proceed();
        } catch (final Throwable e) {
            throw Throwables.propagate(e);
        }
    }
}
