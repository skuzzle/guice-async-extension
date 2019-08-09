package de.skuzzle.inject.async.schedule;

/**
 * A runnable which is locked until {@link #release()} is called. After construction,
 * execution of this runnable is locked and can thus not start until the release method is
 * manually called. Releasing an instance multiple times is allowed since 1.1.0 and has no
 * further effect.
 *
 * @author Simon Taddiken
 */
public interface LockableRunnable extends Runnable {

    /**
     * Wraps the given Runnable into a {@link LockableRunnable}.
     *
     * @param runnable The runnable to wrap.
     * @return The {@link LockableRunnable}.
     */
    public static LockableRunnable locked(Runnable runnable) {
        return Runnables.LatchLockableRunnable.locked(runnable);
    }

    /**
     * Releases the lock which causes this runnable to block. Note: since 1.1.0 this
     * method may be called multiple times without throwing an exception.
     *
     * @return This.
     */
    LockableRunnable release();

}
