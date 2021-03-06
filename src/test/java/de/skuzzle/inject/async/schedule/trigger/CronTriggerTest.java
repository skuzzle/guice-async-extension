package de.skuzzle.inject.async.schedule.trigger;

import org.junit.Test;

import com.google.inject.Guice;

import de.skuzzle.inject.async.guice.DefaultFeatures;
import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.schedule.annotation.CronTrigger;
import de.skuzzle.inject.async.schedule.annotation.OnError;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;

public class CronTriggerTest {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    @Test
    public void testCallExceptionHandler() throws Exception {
        final AbstractMethodHolder methods = new AbstractMethodHolder() {
            @Override
            @Scheduled
            @CronTrigger("0/1 * * * * ?")
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
            @CronTrigger("0/1 * * * * ?")
            protected void threeTimes() {
                super.threeTimes();
            }
        };

        Guice.createInjector(GuiceAsync.createModuleWithFeatures(DefaultFeatures.SCHEDULE), methods);
        methods.waitFor(methods.threeTimes);
    }

    @Test
    public void testCancel() throws Exception {
        final AbstractMethodHolder methods = new AbstractMethodHolder() {
            @Override
            @Scheduled
            @CronTrigger("0/1 * * * * ?")
            protected void cancel(ScheduledContext ctx) {
                super.cancel(ctx);
            }
        };

        Guice.createInjector(GuiceAsync.createModuleWithFeatures(DefaultFeatures.SCHEDULE), methods);
        // because the second count down should never happen
        methods.expectTimeoutFor(methods.cancel);
    }
}
