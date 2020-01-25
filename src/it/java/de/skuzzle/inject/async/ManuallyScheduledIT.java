package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

import de.skuzzle.inject.async.guice.GuiceAsync;
import de.skuzzle.inject.async.guice.ScheduleFeature;
import de.skuzzle.inject.async.schedule.ScheduleProperties;
import de.skuzzle.inject.async.schedule.SchedulingService;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;
import de.skuzzle.inject.async.schedule.annotation.SimpleTrigger;

public class ManuallyScheduledIT {

    private volatile CountDownLatch manualLatch = new CountDownLatch(2);
    private volatile int counterManual;

    @Inject
    private SchedulingService schedulingService;

    @Scheduled
    @SimpleTrigger(100)
    public void testManual() {
        ++counterManual;
        manualLatch.countDown();
    }

    @Test
    public void testManuallyStart() throws Exception {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                final ScheduleProperties disableAutoScheduling = ScheduleProperties.defaultProperties()
                        .disableAutoScheduling();

                GuiceAsync.enableFeaturesFor(binder(),
                        ScheduleFeature.withProperties(disableAutoScheduling));
            }
        }).injectMembers(this);

        schedulingService.startManualScheduling();
        manualLatch.await();
        assertTrue(counterManual > 0);
    }

    @Test
    public void testDoNotManuallyStart() throws Exception {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                final ScheduleProperties disableAutoScheduling = ScheduleProperties.defaultProperties()
                        .disableAutoScheduling();

                GuiceAsync.enableFeaturesFor(binder(),
                        ScheduleFeature.withProperties(disableAutoScheduling));
            }
        }).injectMembers(this);

        // this thing waits forever because scheduler is never started
        final Thread waitForTimeout = new Thread(() -> {
            try {
                manualLatch.await();
                // XXX: this would not fail the test because it occurs in wrong thread
                fail();
            } catch (final InterruptedException ignore) {
            }
        });

        waitForTimeout.start();
        Thread.sleep(1000);
        waitForTimeout.interrupt();
        assertEquals(0, counterManual);
    }

}
