package de.skuzzle.inject.async.internal.runnables;

import static com.google.common.base.Preconditions.checkArgument;

import de.skuzzle.inject.async.ScheduledContext;

class ScopedRunnable implements Runnable {

    private final Runnable wrapped;
    private final ScheduledContext context;

    private ScopedRunnable(Runnable wrapped, ScheduledContext context) {
        this.wrapped = wrapped;
        this.context = context;
    }

    static Runnable of(Runnable wrapped, ScheduledContext context) {
        checkArgument(wrapped != null, "wrapped is null");
        checkArgument(context != null, "context is null");
        return new ScopedRunnable(wrapped, context);
    }

    @Override
    public void run() {
        try {
            this.context.beginNewExecution();
            this.wrapped.run();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            this.context.finishExecution();
        }
    }

}
