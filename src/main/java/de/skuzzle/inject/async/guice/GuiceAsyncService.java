package de.skuzzle.inject.async.guice;

import java.util.concurrent.TimeUnit;

/**
 * Allows to control some aspects of how the async-extension works.
 *
 * @author Simon Taddiken
 * @since 1.2.0
 */
public interface GuiceAsyncService {

    /**
     * Performs an orderly shutdown of the internally used thread pools, using a best
     * effort approach for stopping all active tasks. When supplied a timeout &gt; 0 the
     * current thread will block until all threads have terminated or the timeout has been
     * reached. As the framework uses two internal executors, the timeout will be waited
     * for twice!
     *
     * @param timeout The time that the threads are given to terminate.
     * @param timeUnit The unit of above timeout value.
     * @return <code>true</code> if all threads terminated within the provided timeout,
     *         <code>false</code> if not or if the current thread has been interrupted
     *         while waiting for the timeout.
     * @since 1.2.0
     */
    boolean shutdown(long timeout, TimeUnit timeUnit);
}
