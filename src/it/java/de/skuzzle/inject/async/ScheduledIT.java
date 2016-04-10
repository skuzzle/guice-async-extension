package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.DelayedTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;
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
        public void delayedTrigger() {
            delayedLatch.countDown();
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
            }
        });
    }

    @Test(timeout = 30000)
    public void testExecuteMultipleTimes() throws Exception {
        cronLatch.await();
        simpleLatch.await();
        //delayedLatch.await();
    }
}
