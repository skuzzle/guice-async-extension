package de.skuzzle.inject.async.internal.runnables;

/**
 * A runnable which is locked until {@link #release()} is called. After construction,
 * execution of this runnable is locked and can thus not start until the release method is
 * manually called. Releasing an instance multiple times is an error and will cause a
 * runtime exception.
 *
 * @author Simon Taddiken
 */
public interface LockableRunnable extends Runnable {

    /**
     * Releases the lock which causes this runnable to block. May only be called once.
     */
    void release();

}
