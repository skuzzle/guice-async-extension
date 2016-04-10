package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;

import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.DelayedTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.annotation.Scheduler;
import de.skuzzle.inject.async.annotation.SimpleTrigger;

public class ScheduledIT {

    private static volatile CountDownLatch cronLatch = new CountDownLatch(2);
    private static volatile CountDownLatch simpleLatch = new CountDownLatch(2);
    private static volatile CountDownLatch delayedLatch = new CountDownLatch(1);

    public static class TypeWithScheduledMethods {

        @Scheduled
        @CronTrigger("0/5 * * * * ?")
        public void scheduledSyso(String s) {
            assertEquals("foobar", s);
            cronLatch.countDown();
        }

        @Scheduled
        @SimpleTrigger(5000)
        public void simpleTrigger(String s) {
            assertEquals("foobar", s);
            simpleLatch.countDown();
        }

        @Scheduled
        @DelayedTrigger(5000)
        public void delayedTrigger(@Named("xxx") String s) {
            assertEquals("abc", s);
            delayedLatch.countDown();
        }

        @Scheduled
        @DelayedTrigger(1000)
        @Scheduler(ScheduledExecutorService.class)
        public void throwingException() {
            throw new UnsupportedOperationException();
        }
    }

    @Before
    public void setup() {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                GuiceAsync.enableFor(binder());
                bind(TypeWithScheduledMethods.class).asEagerSingleton();
                bind(String.class).toInstance("foobar");
                bind(String.class).annotatedWith(Names.named("xxx")).toInstance("abc");
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
        });
    }

    @Test(timeout = 30000)
    public void testExecuteMultipleTimes() throws Exception {
        cronLatch.await();
        simpleLatch.await();
        delayedLatch.await();
    }
}
