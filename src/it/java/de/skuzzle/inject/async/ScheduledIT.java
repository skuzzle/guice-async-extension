package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.schedule.ExceptionHandler;
import de.skuzzle.inject.async.schedule.ExecutionContext;
import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.schedule.annotation.CronTrigger;
import de.skuzzle.inject.async.schedule.annotation.CronType;
import de.skuzzle.inject.async.schedule.annotation.DelayedTrigger;
import de.skuzzle.inject.async.schedule.annotation.ExecutionScope;
import de.skuzzle.inject.async.schedule.annotation.OnError;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;
import de.skuzzle.inject.async.schedule.annotation.ScheduledScope;
import de.skuzzle.inject.async.schedule.annotation.Scheduler;
import de.skuzzle.inject.async.schedule.annotation.SimpleTrigger;

public class ScheduledIT {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    private static volatile CountDownLatch cronLatch = new CountDownLatch(2);
    private static volatile CountDownLatch simpleLatch = new CountDownLatch(2);
    private static volatile CountDownLatch delayedLatch = new CountDownLatch(2);
    private static volatile int counterSimpl;
    private static volatile int counterCron;

    public static class TypeWithScheduledMethods {

        @Scheduled
        @DelayedTrigger(5000)
        private static void scheduledPrivateStaticMethod() {
            delayedLatch.countDown();
        }

        @Scheduled
        @CronTrigger("0/5 * * * * ?")
        private void scheduledSyso(String s, ScheduledContext ctx,
                ExecutionContext execCtx,
                @Named("exec") SomeClass executionScoped,
                @Named("sched") SomeClass scheduledScoped) {
            assertEquals("foobar", s);
            cronLatch.countDown();
        }

        @Scheduled
        @SimpleTrigger(5000)
        public void simpleTrigger(String s, ScheduledContext ctx,
                ExecutionContext execCtx,
                @Named("exec") SomeClass executionScoped,
                @Named("sched") SomeClass scheduledScoped) {

            assertEquals("foobar", s);
            simpleLatch.countDown();
            throw new RuntimeException();
        }

        @Scheduled
        @DelayedTrigger(5000)
        public void delayedTrigger(@Named("xxx") String s, ScheduledContext ctx,
                ExecutionContext execCtx,
                @Named("exec") SomeClass executionScoped,
                @Named("sched") SomeClass scheduledScoped) {
            assertEquals("abc", s);
            delayedLatch.countDown();
        }

        @Scheduled
        @DelayedTrigger(1000)
        @Scheduler(ScheduledExecutorService.class)
        public void throwingException() {
            throw new UnsupportedOperationException();
        }

        @Scheduled
        @DelayedTrigger(1000)
        @Scheduler(ScheduledExecutorService.class)
        @OnError(TestExceptionHandler.class)
        public void throwingExceptionWithCustomHandler() {
            throw new UnsupportedOperationException();
        }

        @Scheduled
        @SimpleTrigger(500)
        public void testCancel(ScheduledContext ctx) {
            ++counterSimpl;
            ctx.cancel(true);
        }

        @Scheduled
        @CronTrigger(value = "0/5 * * * * ?", cronType = CronType.QUARTZ)
        public void testCancelCron(ScheduledContext ctx) {
            ++counterCron;
            ctx.cancel(false);
        }
    }

    public static class SomeClass {

    }

    public static class TestExceptionHandler implements ExceptionHandler {

        private volatile int count = 0;

        @Override
        public void onException(Exception e) {
            this.count++;
        }

        public int getCount() {
            return this.count;
        }

    }

    @Inject
    private TestExceptionHandler testExceptionHandler;

    @Before
    public void setup() {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                GuiceAsync.enableFor(binder());
                bind(TestExceptionHandler.class).asEagerSingleton();
                bind(TypeWithScheduledMethods.class).asEagerSingleton();
                bind(String.class).toInstance("foobar");
                bind(String.class).annotatedWith(Names.named("xxx")).toInstance("abc");
                bind(SomeClass.class)
                        .annotatedWith(Names.named("exec"))
                        .to(SomeClass.class)
                        .in(ExecutionScope.class);
                bind(SomeClass.class)
                        .annotatedWith(Names.named("sched"))
                        .to(SomeClass.class)
                        .in(ScheduledScope.class);
            }

            @Provides
            public ThreadFactory threadFactory() {
                return new ThreadFactoryBuilder()
                        .setNameFormat("it-thread")
                        .build();
            }

            @Provides
            public ScheduledExecutorService scheduler(ThreadFactory factory) {
                return Executors.newScheduledThreadPool(4, factory);
            }
        }).injectMembers(this);
    }

    @Test(timeout = 30000)
    public void testExecuteMultipleTimes() throws Exception {
        cronLatch.await();
        simpleLatch.await();
        delayedLatch.await();
        assertEquals("cancel might not have worked if counter > 1", 1, counterSimpl);
        assertEquals("cancel might not have worked if counter > 1", 1, counterCron);
        assertEquals(1, this.testExceptionHandler.getCount());
    }
}
