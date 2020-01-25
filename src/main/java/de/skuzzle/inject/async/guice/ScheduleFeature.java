package de.skuzzle.inject.async.guice;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Injector;

import de.skuzzle.inject.async.schedule.ScheduleModule;
import de.skuzzle.inject.async.schedule.ScheduleProperties;

/**
 * Specialized {@link Feature} that allows customization of the scheduling behavior by
 * providing a custom instance of {@link ScheduleProperties}.
 * <p>
 *
 * @author Simon Taddiken
 * @since 2.0.0
 * @see DefaultFeatures#SCHEDULE
 */
public class ScheduleFeature implements Feature {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleFeature.class);

    public static final ScheduleFeature DEFAULT = ScheduleFeature
            .withProperties(ScheduleProperties.defaultProperties());

    private final ScheduleProperties scheduleProperties;

    private ScheduleFeature(ScheduleProperties scheduleProperties) {
        Preconditions.checkArgument(scheduleProperties != null, "scheduleProperties must not be null");
        this.scheduleProperties = scheduleProperties;
    }

    /**
     * Creates the feature and uses the given {@link ScheduleProperties}.
     *
     * @param scheduleProperties The properties.
     * @return The Feature instance that can be passed to {@link GuiceAsync} during
     *         initialization.
     */
    public static ScheduleFeature withProperties(ScheduleProperties scheduleProperties) {
        return new ScheduleFeature(scheduleProperties);
    }

    @Override
    public void installModuleTo(Binder binder, GuiceAsync principal) {
        binder.install(new ScheduleModule(principal, scheduleProperties));
    }

    @Override
    public boolean cleanupExecutor(Injector injector, long timeout, TimeUnit timeUnit) {
        final ScheduledExecutorService scheduler = injector.getInstance(Keys.DEFAULT_SCHEDULER_KEY);
        if (!Shutdown.executor(scheduler, timeout, timeUnit)) {
            LOG.warn("There are still active tasks lingering in default scheduler after shutdown. Wait time: {} {}",
                    timeout, timeUnit);
            return false;
        }
        return true;
    }
}
