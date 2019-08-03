package de.skuzzle.inject.async.internal.context;

import java.lang.reflect.Method;

import de.skuzzle.inject.async.ScheduledContext;

class ContextFactoryImpl implements ContextFactory {

    @Override
    public ScheduledContext createContext(Method method, Object self) {
        return new ScheduledContextImpl(method, self);
    }

}
