package de.skuzzle.inject.async.guice;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

class GuiceAsyncServiceImpl implements GuiceAsyncService {

    private static final Logger LOG = LoggerFactory.getLogger(GuiceAsyncServiceImpl.class);

    private final Injector injector;
    private final Set<Feature> features;

    @Inject
    public GuiceAsyncServiceImpl(Injector injector, @DefaultBinding Set<Feature> features) {
        this.injector = injector;
        this.features = features;
    }

    @Override
    public boolean shutdown(long timeout, TimeUnit timeUnit) {
        boolean result = true;

        if (features.contains(Feature.ASYNC)) {
            final ExecutorService executor = injector.getInstance(Keys.DEFAULT_EXECUTOR_KEY);
            if (!shutdownExecutor(executor, timeout, timeUnit)) {
                LOG.warn("There are still active tasks lingering in default executor after shutdown. Wait time: {} {}",
                        timeout, timeUnit);
                result = false;
            }
        }
        if (features.contains(Feature.SCHEDULE)) {
            final ScheduledExecutorService scheduler = injector.getInstance(Keys.DEFAULT_SCHEDULER_KEY);
            if (!shutdownExecutor(scheduler, timeout, timeUnit)) {
                LOG.warn("There are still active tasks lingering in default scheduler after shutdown. Wait time: {} {}",
                        timeout, timeUnit);
                result = false;
            }
        }
        return result;
    }

    private boolean shutdownExecutor(ExecutorService executor, long timeout, TimeUnit timeUnit) {
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
