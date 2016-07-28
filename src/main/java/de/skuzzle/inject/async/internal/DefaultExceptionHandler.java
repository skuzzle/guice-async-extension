package de.skuzzle.inject.async.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.internal.context.ScheduledContextHolder;

class DefaultExceptionHandler implements ExceptionHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultExceptionHandler.class);

    @Override
    public void onException(Exception exception) {
        if (ScheduledContextHolder.isContextActive()) {
            final ScheduledContext ctx = ScheduledContextHolder.getContext();
            LOG.error("Unexpected error while executing a scheduled method. Context: {}",
                    ctx, exception);
        } else {
            LOG.error("Unexpected error occurred while executing scheduled method. "
                    + "Note: there is no ScheduledContext information available. "
                    + "Either the TriggerStrategy in place does not support scoped "
                    + "executions or it may be buggy.",
                    exception);
        }
    }
}
