package de.skuzzle.inject.async.internal.trigger;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.annotation.Scheduler;
import de.skuzzle.inject.async.annotation.SimpleScheduleType;
import de.skuzzle.inject.async.annotation.SimpleTrigger;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;

@RunWith(MockitoJUnitRunner.class)
public class SimpleTriggerStrategyTest {

    @Mock
    private Injector injector;
    @Mock
    private RunnableBuilder runnableBuilder;
    @InjectMocks
    private SimpleTriggerStrategy subject;

    @Mock
    private ScheduledExecutorService executorService;


    @Scheduled
    @Scheduler(ScheduledExecutorService.class)
    @SimpleTrigger(value = 5000,
    initialDelay = 12,
    scheduleType = SimpleScheduleType.WITH_FIXED_DELAY,
    timeUnit = TimeUnit.HOURS)
    public void methodWithSimpleTrigger() {

    }

    @Scheduled
    @Scheduler(ScheduledExecutorService.class)
    public void methodWithoutTrigger() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoTrigger() throws Exception {
        final Method method = getClass().getMethod("methodWithoutTrigger");
        this.subject.schedule(method, this, this.executorService);

    }

    @Test
    public void testSchedule() throws Exception {
        final Method method = getClass().getMethod("methodWithSimpleTrigger");

        final Runnable invoke = mock(Runnable.class);
        final Runnable scoped = mock(Runnable.class);
        final Runnable skip = mock(Runnable.class);

        when(this.runnableBuilder.invoke(Mockito.any())).thenReturn(invoke);
        when(this.runnableBuilder.scope(eq(invoke), Mockito.any())).thenReturn(scoped);
        when(this.runnableBuilder.skip(eq(scoped), Mockito.any())).thenReturn(skip);

        this.subject.schedule(method, this, this.executorService);
        verify(this.executorService).scheduleWithFixedDelay(
                eq(skip), eq(12L), eq(5000L), eq(TimeUnit.HOURS));
    }
}
