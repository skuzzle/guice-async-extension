package de.skuzzle.inject.async.schedule;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;

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
            this.context.beginNewExecution();
            this.wrapped.run();
        } finally {
            this.context.finishExecution();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("wrapped", wrapped)
                .add("context", context)
                .toString();
    }
}
