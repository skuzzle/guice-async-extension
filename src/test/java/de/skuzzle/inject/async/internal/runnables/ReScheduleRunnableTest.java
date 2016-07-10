package de.skuzzle.inject.async.internal.runnables;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.cronutils.model.time.ExecutionTime;

@RunWith(MockitoJUnitRunner.class)
public class ReScheduleRunnableTest {

    @Mock
    private ScheduledExecutorService executor;
    @Mock
    private ExecutionTime executionTime;
    @Mock
    private Runnable invocation;
    @InjectMocks
    private ReScheduleRunnable subject;

    @Before
    public void setUp() throws Exception {
        final Duration toNext = Duration.millis(5000);
        when(this.executionTime.timeToNextExecution(Mockito.any())).thenReturn(toNext);
    }

    @Test
    public void testRun() throws Exception {
        this.subject.run();
        verify(this.executor).schedule(this.subject, 5000, TimeUnit.MILLISECONDS);
    }
}
