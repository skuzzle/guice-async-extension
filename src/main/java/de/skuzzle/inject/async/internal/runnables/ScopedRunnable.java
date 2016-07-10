package de.skuzzle.inject.async.internal.runnables;

import static com.google.common.base.Preconditions.checkArgument;

import de.skuzzle.inject.async.internal.context.ScheduledContextImpl;

class ScopedRunnable implements Runnable {

    private final Runnable wrapped;
    private final ScheduledContextImpl context;

    private ScopedRunnable(Runnable wrapped, ScheduledContextImpl context) {
        this.wrapped = wrapped;
        this.context = context;
    }

    static Runnable of(Runnable wrapped, ScheduledContextImpl context) {
        checkArgument(wrapped != null, "wrapped is null");
        checkArgument(context != null, "context is null");
        return new ScopedRunnable(wrapped, context);
    }

    @Override
    public void run() {
        try {
            context.beginNewExecution();
            wrapped.run();
        } finally {
            context.finishExecution();
        }
    }

}
