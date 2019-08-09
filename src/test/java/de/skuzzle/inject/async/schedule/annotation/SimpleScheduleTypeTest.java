package de.skuzzle.inject.async.schedule.annotation;

import static org.mockito.Mockito.verify;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.skuzzle.inject.async.schedule.annotation.SimpleScheduleType;

@RunWith(MockitoJUnitRunner.class)
public class SimpleScheduleTypeTest {

    @Mock
    private ScheduledExecutorService scheduler;
    @Mock
    private Runnable command;

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testAtFixedRate() throws Exception {
        SimpleScheduleType.AT_FIXED_RATE.schedule(this.scheduler, this.command, 5, 6,
                TimeUnit.MINUTES);
        verify(this.scheduler).scheduleAtFixedRate(this.command, 5, 6, TimeUnit.MINUTES);
    }

    @Test
    public void testWithFixedDelay() throws Exception {
        SimpleScheduleType.WITH_FIXED_DELAY.schedule(this.scheduler, this.command, 5, 6,
                TimeUnit.MINUTES);
        verify(this.scheduler).scheduleWithFixedDelay(this.command, 5, 6,
                TimeUnit.MINUTES);
    }
}
