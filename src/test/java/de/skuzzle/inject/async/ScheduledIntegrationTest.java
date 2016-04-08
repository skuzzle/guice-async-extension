package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;

public class ScheduledIntegrationTest {

    private static volatile int invocationCount = 0;

    public static class TypeWithScheduledMethods {

        @Scheduled
        @CronTrigger("0/10 * * * * ?")
        public void scheduledSyso() {
            invocationCount++;
        }
    }

    @Before
    public void setup() {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                GuiceAsync.enableFor(binder());
                bind(TypeWithScheduledMethods.class).asEagerSingleton();
            }
        });
    }

    @Test
    public void testo_O() throws Exception {
        Thread.sleep(62000);
        assertEquals(5, invocationCount);
    }
}
