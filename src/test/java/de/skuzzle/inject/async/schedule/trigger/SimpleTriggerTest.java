package de.skuzzle.inject.async.schedule.trigger;

import org.junit.Test;

import com.google.inject.Guice;

import de.skuzzle.inject.async.guice.Feature;
import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.schedule.annotation.OnError;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;
import de.skuzzle.inject.async.schedule.annotation.SimpleTrigger;

public class SimpleTriggerTest {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    @Test
    public void testCallExceptionHandler() throws Exception {
        final AbstractMethodHolder methods = new AbstractMethodHolder() {
            @Override
            @Scheduled
            @SimpleTrigger(1000)
            @OnError(MockExceptionHandler.class)
            protected void throwingException() {
                super.throwingException();
            }
        };

        Guice.createInjector(GuiceAsync.createModuleWithFeatures(Feature.SCHEDULE), methods);
        methods.waitFor(methods.throwingException);
        methods.exceptionHandler.assertExceptionThrown(RuntimeException.class);
    }

    @Test
    public void testCallRecurringly() throws Exception {
        final AbstractMethodHolder methods = new AbstractMethodHolder() {
            @Override
            @Scheduled
            @SimpleTrigger(1000)
            protected void threeTimes() {
                super.threeTimes();
            }
        };

        Guice.createInjector(GuiceAsync.createModuleWithFeatures(Feature.SCHEDULE), methods);
        methods.waitFor(methods.threeTimes);
    }

    @Test
    public void testCancel() throws Exception {
        final AbstractMethodHolder methods = new AbstractMethodHolder() {
            @Override
            @Scheduled
            @SimpleTrigger(1000)
            protected void cancel(ScheduledContext ctx) {
                super.cancel(ctx);
            }
        };

        Guice.createInjector(GuiceAsync.createModuleWithFeatures(Feature.SCHEDULE), methods);
        // because the second count down should never happen
        methods.expectTimeoutFor(methods.cancel);
    }
}
