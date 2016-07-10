package de.skuzzle.inject.async.internal.runnables;

import de.skuzzle.inject.async.internal.context.ScheduledContextImpl;

class SkipRunnable implements Runnable {

    private final Runnable wrapped;
    private final ScheduledContextImpl context;
    
    private SkipRunnable(Runnable wrapped, ScheduledContextImpl context) {
        this.wrapped = wrapped;
        this.context = context;
    }
    
    static Runnable of(Runnable wrapped, ScheduledContextImpl context) {
        return new SkipRunnable(wrapped, context);
    }

    @Override
    public void run() {
        if (!context.isStopRequested()) {
            wrapped.run();
        }
    }

}
