package de.skuzzle.inject.async;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.schedule.annotation.DelayedTrigger;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;
import de.skuzzle.inject.async.schedule.annotation.Scheduler;

public class CallerRunsStrategySchedulingTest {

    public static class TypeWithScheduledMethods {

        static volatile boolean firstCalled;
        static volatile boolean secondCalled;

        @Scheduled
        @Scheduler(ScheduledExecutorService.class)
        @Named("only-one-thread")
        @DelayedTrigger(0)
        public static void takesVeryLong() {
            try {
                System.out.println("First");
                firstCalled = true;
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Scheduled
        @Scheduler(ScheduledExecutorService.class)
        @Named("only-one-thread")
        @DelayedTrigger(500)
        public static void scheduleSecond() {
            secondCalled = true;
            System.out.println("second");
        }
    }

    @Inject
    private TypeWithScheduledMethods typeWithScheduledMethods;
    @Inject
    @Named("only-one-thread")
    private ScheduledExecutorService executor;

    @Before
    public void setup() {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                GuiceAsync.enableFor(binder());
            }

            @Provides
            public ThreadFactory threadFactory() {
                return new ThreadFactoryBuilder()
                        .setNameFormat("it-thread")
                        .build();
            }

            @Provides
            @Singleton
            @Named("only-one-thread")
            public ScheduledExecutorService scheduler(ThreadFactory factory) {
                return new ScheduledThreadPoolExecutor(1, factory,
                        new CallerRunsPolicy());
            }
        }).injectMembers(this);
    }

    @Test
    public void test() throws Exception {
        this.executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        assertTrue(TypeWithScheduledMethods.firstCalled);
        assertTrue(TypeWithScheduledMethods.secondCalled);
    }
}
