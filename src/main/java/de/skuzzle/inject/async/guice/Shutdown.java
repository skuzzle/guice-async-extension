package de.skuzzle.inject.async.guice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Shutdown {

    private static final Logger LOG = LoggerFactory.getLogger(Shutdown.class);

    static boolean executor(ExecutorService executor, long timeout, TimeUnit timeUnit) {
        LOG.debug("Shutting down guice-async default executor instance {}", executor);
        executor.shutdownNow();

        try {
            return executor.awaitTermination(timeout, timeUnit);
        } catch (final InterruptedException e) {
            final Thread currentThread = Thread.currentThread();
            LOG.error("Thread {} interrupted while waiting to shutdown guice-async default executor",
                    currentThread.getName(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
