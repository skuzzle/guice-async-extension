package de.skuzzle.inject.async.guice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Injector;

import de.skuzzle.inject.async.methods.AsyncModule;
import de.skuzzle.inject.async.methods.annotation.Async;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;

/**
 * Supported features that can be passed when initializing the async/scheduling subsystem.
 * Each feature is self contained and has no dependence to other features being present.
 *
 * @author Simon Taddiken
 * @since 2.0.0
 */
public enum DefaultFeatures implements Feature {
    /** This feature enables handling of the {@link Async} annotation. */
    ASYNC {

        @Override
        public void installModuleTo(Binder binder, GuiceAsync principal) {
            binder.install(new AsyncModule(principal));
        }

        @Override
        public boolean cleanupExecutor(Injector injector, long timeout, TimeUnit timeUnit) {
            final ExecutorService executor = injector.getInstance(Keys.DEFAULT_EXECUTOR_KEY);
            if (!Shutdown.executor(executor, timeout, timeUnit)) {
                LOG.warn("There are still active tasks lingering in default executor after shutdown. Wait time: {} {}",
                        timeout, timeUnit);
                return false;
            }
            return true;
        }
    },
    /**
     * This feature enables handling of the {@link Scheduled} annotation. You may also use
     * an instance of {@link ScheduleFeature} instead of this instance (but better do not
     * provide both).
     */
    SCHEDULE {
        @Override
        public void installModuleTo(Binder binder, GuiceAsync principal) {
            ScheduleFeature.DEFAULT.installModuleTo(binder, principal);
        }

        @Override
        public boolean cleanupExecutor(Injector injector, long timeout, TimeUnit timeUnit) {
            return ScheduleFeature.DEFAULT.cleanupExecutor(injector, timeout, timeUnit);
        }
    };

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFeatures.class);

}
