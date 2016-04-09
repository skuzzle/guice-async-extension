package de.skuzzle.inject.async.annotation;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Defines the way in which a method annotated with {@link SimpleTrigger} is
 * scheduled with a {@link ScheduledExecutorService}.
 *
 * @author Simon Taddiken
 */
public enum SimpleScheduleType {
    /**
     * Used to schedule a command using
     * {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * .
     */
    AT_FIXED_RATE {

        @Override
        public void schedule(ScheduledExecutorService scheduler, Runnable command,
                long initialDelay, long period, TimeUnit unit) {
            scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

    },
    /**
     * Used to schedule a command using
     * {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     */
    WITH_FIXED_DELAY {
        @Override
        public void schedule(ScheduledExecutorService scheduler, Runnable command,
                long initialDelay, long delay, TimeUnit unit) {
            scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    };

    public abstract void schedule(ScheduledExecutorService scheduler, Runnable command,
            long initialDelay, long rate, TimeUnit unit);
}
