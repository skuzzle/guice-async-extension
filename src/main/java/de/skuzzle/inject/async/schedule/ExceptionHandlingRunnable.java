package de.skuzzle.inject.async.schedule;

import com.google.common.base.MoreObjects;

class ExceptionHandlingRunnable implements Runnable {

    private final Runnable wrapped;
    private final ExceptionHandler handler;

    private ExceptionHandlingRunnable(Runnable wrapped, ExceptionHandler handler) {
        this.wrapped = wrapped;
        this.handler = handler;
    }

    public static Runnable of(Runnable wrapped, ExceptionHandler handler) {
        return new ExceptionHandlingRunnable(wrapped, handler);
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
