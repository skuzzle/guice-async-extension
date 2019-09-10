package de.skuzzle.inject.async.schedule.annotation;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
        public ScheduledFuture<?> schedule(ScheduledExecutorService scheduler, 
                Runnable command, long initialDelay, long period, TimeUnit unit) {
            return scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

    },
    /**
     * Used to schedule a command using
     * {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     */
    WITH_FIXED_DELAY {
        @Override
        public ScheduledFuture<?> schedule(ScheduledExecutorService scheduler, 
                Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    };

    /**
     * Schedules a {@link Runnable} according to this scheduling type.
     *
     * @param scheduler The scheduler to schedule the command with.
     * @param command The command to schedule.
     * @param initialDelay The initial delay.
     * @param rate The scheduling rate.
     * @param unit Time unit in which rate and delay are interpreted.
     * @return The future object.
     */
    public abstract ScheduledFuture<?> schedule(ScheduledExecutorService scheduler, 
            Runnable command, long initialDelay, long rate, TimeUnit unit);
}
