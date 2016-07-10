package de.skuzzle.inject.async.internal.context;

import de.skuzzle.inject.async.ScheduledContext;

class ContextFactoryImpl implements ContextFactory {

    @Override
    public ScheduledContext createContext() {
        return new ScheduledContextImpl();
    }

}
