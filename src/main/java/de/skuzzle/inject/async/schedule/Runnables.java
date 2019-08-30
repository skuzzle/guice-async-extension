package de.skuzzle.inject.async.schedule;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

final class Runnables {

    /**
     * Creates a runnable that executes the given invocation in the scope of given context
     * and handles exceptions using the given handler.
     *
     * The returned {@link LockableRunnable} will be locked by default AND MUST BU
     * manually {@link LockableRunnable#release() unlocked} after scheduling it with the
     * {@link ScheduledExecutorService}.
     *
     * @param invocation The method invocation that is to be scheduled.
     * @param context The schedule context.
     * @param handler The exception handler.
     * @return The locked runnable.
     */
    static LockableRunnable createLockedRunnableStack(InjectedMethodInvocation invocation,
            ScheduledContextImpl context, ExceptionHandler handler) {
        return LatchLockableRunnable.locked(ScopedRunnable.of(
                ExceptionHandlingRunnable.of(
                        InvokeMethodRunnable.of(
                                invocation),
                        handler),
                context));
    }

    private static final Logger LOG = LoggerFactory.getLogger(LatchLockableRunnable.class);

    static class LatchLockableRunnable implements LockableRunnable {

        private final Runnable runnable;
        private final CountDownLatch latch;

        private LatchLockableRunnable(Runnable runnable) {
            this.runnable = runnable;
            this.latch = new CountDownLatch(1);
        }

        public static LockableRunnable locked(Runnable runnable) {
            return new LatchLockableRunnable(runnable);
        }

        @Override
        public void run() {
            try {
                LOG.trace("Waiting for approval");
                this.latch.await();
                LOG.trace("Executing wrapped runnable: {}", runnable);
                this.runnable.run();
            } catch (final InterruptedException e) {
                LOG.error("Interrupted while waiting to begin execution. Execution of {} has been skipped.",
                        this.runnable, e);
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public LockableRunnable release() {
            this.latch.countDown();
            return this;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("runnable", runnable)
                    .toString();
        }

    }

    /**
     * Runnable that wraps another and executes it in scope of the given
     * {@link ScheduledContext}.
     *
     * @author Simon Taddiken
     */
    private static class ScopedRunnable implements Runnable {

        private final Runnable wrapped;
        private final ScheduledContextImpl context;

        private ScopedRunnable(Runnable wrapped, ScheduledContextImpl context) {
            this.wrapped = wrapped;
            this.context = context;
        }

        static Runnable of(Runnable wrapped, ScheduledContextImpl context) {
            checkArgument(wrapped != null, "wrapped is null");
            checkArgument(context != null, "context is null");
            return new ScopedRunnable(wrapped, context);
        }

        @Override
        public void run() {
            try {
                this.context.beginNewExecution();
                this.wrapped.run();
            } finally {
                this.context.finishExecution();
            }
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("wrapped", wrapped)
                    .add("context", context)
                    .toString();
        }
    }

    /**
     * Runnable that wraps another and delegates exceptions to an exception handler.
     *
     * @author Simon Taddiken
     */
    private static class ExceptionHandlingRunnable implements Runnable {

        private final Runnable wrapped;
        private final ExceptionHandler handler;

        private ExceptionHandlingRunnable(Runnable wrapped, ExceptionHandler handler) {
            this.wrapped = wrapped;
            this.handler = handler;
        }

        public static Runnable of(Runnable wrapped, ExceptionHandler handler) {
            return new ExceptionHandlingRunnable(wrapped, handler);
        }

        @Override
        public void run() {
            try {
                this.wrapped.run();
            } catch (final Exception e) {
                this.handler.onException(e);
            }
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("wrapped", wrapped)
                    .toString();
        }

    }
}
