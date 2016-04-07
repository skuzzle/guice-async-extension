package de.skuzzle.inject.async.annotation;

import java.util.concurrent.ScheduledExecutorService;

public @interface Scheduler {
    Class<? extends ScheduledExecutorService> value();
}
