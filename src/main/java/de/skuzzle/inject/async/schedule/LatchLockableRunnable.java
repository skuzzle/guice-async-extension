package de.skuzzle.inject.async.schedule;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

class LatchLockableRunnable implements LockableRunnable {

    private static final Logger LOG = LoggerFactory.getLogger(LatchLockableRunnable.class);

    private final Runnable runnable;
    private final CountDownLatch latch;

    LatchLockableRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.latch = new CountDownLatch(1);
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
