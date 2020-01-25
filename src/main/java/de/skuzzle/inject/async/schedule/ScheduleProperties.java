package de.skuzzle.inject.async.schedule;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @since 2.0.0
 * @author Simon Taddiken
 */
public final class ScheduleProperties {

    private boolean enableAutoScheduling = true;

    private ScheduleProperties() {

    }

    /**
     * Returns the default properties.
     *
     * @return A new instance with defaults set.
     */
    public static ScheduleProperties defaultProperties() {
        return new ScheduleProperties();
    }

    /**
     * Disables the behavior of automatic method scheduling. If automatic scheduling is
     * disabled, methods are only actually be scheduled with their
     * {@link ScheduledExecutorService} after manually calling
     * {@link SchedulingService#startManualScheduling()}.
     *
     * @return This instance for method chaining.
     */
    public ScheduleProperties disableAutoScheduling() {
        this.enableAutoScheduling = false;
        return this;
    }

    boolean isAutoSchedulingEnabled() {
        return enableAutoScheduling;
    }
}
