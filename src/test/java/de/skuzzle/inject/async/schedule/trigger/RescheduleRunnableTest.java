package de.skuzzle.inject.async.schedule.trigger;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.cronutils.model.time.ExecutionTime;

import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.schedule.trigger.RescheduleRunnable;

@RunWith(MockitoJUnitRunner.class)
public class RescheduleRunnableTest {

    @Mock
    private ScheduledExecutorService executor;
    @Mock
    private ExecutionTime executionTime;
    @Mock
    private Runnable invocation;
    @Mock
    private ScheduledContext context;
    @InjectMocks
    private RescheduleRunnable subject;

    @Before
    public void setUp() throws Exception {
        final Duration toNext = Duration.ofMillis(5000);
        when(this.executionTime.timeToNextExecution(Mockito.any())).thenReturn(Optional.of(toNext));
    }

    @Test
    public void testRun() throws Exception {
        this.subject.run();
        verify(this.executor).schedule(isA(LatchLockableRunnable.class), eq(5100L),
                eq(TimeUnit.MILLISECONDS));
    }
}
