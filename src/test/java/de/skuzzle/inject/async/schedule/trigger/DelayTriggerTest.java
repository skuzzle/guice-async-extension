package de.skuzzle.inject.async.schedule.trigger;

import org.junit.Test;

import com.google.inject.Guice;

import de.skuzzle.inject.async.guice.DefaultFeatures;
import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.schedule.annotation.DelayedTrigger;
import de.skuzzle.inject.async.schedule.annotation.OnError;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;

public class DelayTriggerTest {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    @Test
    public void testCallExceptionHandler() throws Exception {
        final AbstractMethodHolder methods = new AbstractMethodHolder() {
            @Override
            @Scheduled
            @DelayedTrigger(1000)
            @OnError(MockExceptionHandler.class)
            protected void throwingException() {
                super.throwingException();
            }
        };

        Guice.createInjector(GuiceAsync.createModuleWithFeatures(DefaultFeatures.SCHEDULE), methods);
        methods.waitFor(methods.throwingException);
        methods.exceptionHandler.assertExceptionThrown(RuntimeException.class);
    }

    @Test
    public void testCallRecurringly() throws Exception {
        final AbstractMethodHolder methods = new AbstractMethodHolder() {
            @Override
            @Scheduled
            @DelayedTrigger(1000)
            protected void threeTimes() {
                super.threeTimes();
            }
        };

        Guice.createInjector(GuiceAsync.createModuleWithFeatures(DefaultFeatures.SCHEDULE), methods);
        methods.expectTimeoutFor(methods.threeTimes);
    }

    @Test
    public void testCancel() throws Exception {
        // this test doesn't make sense here
    }
}
