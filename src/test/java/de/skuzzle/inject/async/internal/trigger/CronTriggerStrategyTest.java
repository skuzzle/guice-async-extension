package de.skuzzle.inject.async.internal.trigger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.internal.context.ContextFactory;
import de.skuzzle.inject.async.internal.runnables.Reschedulable;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;

@RunWith(MockitoJUnitRunner.class)
public class CronTriggerStrategyTest {

    @Mock
    private Injector injector;
    @Mock
    private RunnableBuilder runnableBuilder;
    @Mock
    private ContextFactory contextFactory;
    @InjectMocks
    private CronTriggerStrategy subject;

    @Mock
    private ScheduledExecutorService executor;
    @Mock
    private ExceptionHandler exceptionHandler;
    @Mock
    private ScheduledContext scheduledContext;

    @Before
    public void setUp() throws Exception {
        when(this.contextFactory.createContext(any())).thenReturn(this.scheduledContext);
    }

    @CronTrigger("0/5 * * * * ?")
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
        this.subject.schedule(method, this, this.executor, this.exceptionHandler);
    }

    @Test
    public void testSchedule() throws Exception {
        final Method method = getClass().getMethod("scheduledMethod");
        final Runnable runnable = mock(Runnable.class);
        final Reschedulable reschedule = mock(Reschedulable.class);

        when(this.runnableBuilder.createRunnableStack(any(), eq(this.scheduledContext),
                eq(this.exceptionHandler))).thenReturn(runnable);

        when(this.runnableBuilder.reschedule(eq(this.scheduledContext), eq(runnable),
                eq(this.executor), any())).thenReturn(reschedule);

        this.subject.schedule(method, this, this.executor, this.exceptionHandler);

        verify(reschedule).scheduleNextExecution();
    }
}
