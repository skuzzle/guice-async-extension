package de.skuzzle.inject.async.internal.trigger;

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

import de.skuzzle.inject.async.util.InjectedMethodInvocation;

@RunWith(MockitoJUnitRunner.class)
public class ReScheduleRunnableTest {

    @Mock
    private ScheduledExecutorService executor;
    @Mock
    private ExecutionTime executionTime;
    @Mock
    private InjectedMethodInvocation invocation;
    @InjectMocks
    private ReScheduleRunnable subject;

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testRun() throws Exception {
        final Duration toNext = Duration.millis(5000);
        when(this.executionTime.timeToNextExecution(Mockito.any())).thenReturn(toNext);
        this.subject.run();
        verify(this.executor).schedule(this.subject, 5000, TimeUnit.MILLISECONDS);
    }
}
