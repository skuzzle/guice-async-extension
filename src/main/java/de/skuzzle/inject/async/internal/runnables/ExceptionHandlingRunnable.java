package de.skuzzle.inject.async.internal.runnables;

import com.google.common.base.MoreObjects;

import de.skuzzle.inject.async.ExceptionHandler;

class ExceptionHandlingRunnable implements Runnable {

    private final Runnable wrapped;
    private final ExceptionHandler handler;

    ExceptionHandlingRunnable(Runnable wrapped, ExceptionHandler handler) {
        this.wrapped = wrapped;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            this.wrapped.run();
        } catch (final Exception e) {
            this.handler.onException(e);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("wrapped", wrapped)
                .toString();
    }

}
