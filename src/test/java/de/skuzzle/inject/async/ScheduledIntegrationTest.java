package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;

public class ScheduledIntegrationTest {

    private static CountDownLatch latch = new CountDownLatch(2);

    public static class TypeWithScheduledMethods {

        @Scheduled
        @CronTrigger("0/5 * * * * ?")
        public void scheduledSyso(String s) {
            assertEquals("foobar", s);
            latch.countDown();
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
        latch.await();
    }
}
