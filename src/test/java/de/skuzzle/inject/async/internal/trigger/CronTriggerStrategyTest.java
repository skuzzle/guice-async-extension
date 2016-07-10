package de.skuzzle.inject.async.internal.trigger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.internal.runnables.Reschedulable;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;

@RunWith(MockitoJUnitRunner.class)
public class CronTriggerStrategyTest {

    @Mock
    private Injector injector;
    @Mock
    private RunnableBuilder runnableBuilder;
    @InjectMocks
    private CronTriggerStrategy subject;

    @Mock
    private ScheduledExecutorService executor;

    @Before
    public void setUp() throws Exception {}

    @CronTrigger("0 0 0 * * *")
    public void scheduledMethod() {

    }

    public void missingTrigger() {

    }

    @Test
    public void testGetTriggerType() throws Exception {
        assertEquals(CronTrigger.class, this.subject.getTriggerType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleMissingTrigger() throws Exception {
        final Method method = getClass().getMethod("missingTrigger");
        this.subject.schedule(method, this, this.executor);
    }

    @Test
    public void testSchedule() throws Exception {
        final Method method = getClass().getMethod("scheduledMethod");
        final Runnable invokeRunnable = mock(Runnable.class);
        final Runnable scopedRunnable = mock(Runnable.class);
        final Reschedulable reschedule = mock(Reschedulable.class);
        
        when(runnableBuilder.invoke(Mockito.any())).thenReturn(invokeRunnable);
        when(runnableBuilder.scope(Mockito.eq(invokeRunnable), Mockito.any()))
                .thenReturn(scopedRunnable);
        when(runnableBuilder.reschedule(Mockito.eq(scopedRunnable), Mockito.eq(executor), 
                Mockito.any(), Mockito.any())).thenReturn(reschedule);
        
        this.subject.schedule(method, this, this.executor);
        
        verify(reschedule).scheduleNextExecution();
    }
}
