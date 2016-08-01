package de.skuzzle.inject.async.internal.runnables;

import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LatchLockableRunnable implements LockableRunnable {

    private static final Logger LOG = LoggerFactory
            .getLogger(LatchLockableRunnable.class);

    private final Runnable runnable;
    private final CountDownLatch latch;

    LatchLockableRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            this.latch.await();
            this.runnable.run();
        } catch (final InterruptedException e) {
            LOG.error("Interrupted while waiting to begin execution. "
                    + "Execution of {} has been skipped.", this.runnable, e);
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public void release() {
        checkState(this.latch.getCount() > 0, "Already released");
        this.latch.countDown();
    }

}
