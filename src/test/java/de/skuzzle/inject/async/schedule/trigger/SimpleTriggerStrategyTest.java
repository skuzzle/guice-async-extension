package de.skuzzle.inject.async.schedule.trigger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import de.skuzzle.inject.async.schedule.ContextFactory;
import de.skuzzle.inject.async.schedule.ExceptionHandler;
import de.skuzzle.inject.async.schedule.LockableRunnable;
import de.skuzzle.inject.async.schedule.RunnableBuilder;
import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;
import de.skuzzle.inject.async.schedule.annotation.Scheduler;
import de.skuzzle.inject.async.schedule.annotation.SimpleScheduleType;
import de.skuzzle.inject.async.schedule.annotation.SimpleTrigger;
import de.skuzzle.inject.async.schedule.trigger.SimpleTriggerStrategy;

@RunWith(MockitoJUnitRunner.class)
public class SimpleTriggerStrategyTest {

    @Mock
    private Injector injector;
    @Mock
    private RunnableBuilder runnableBuilder;
    @Mock
    private ContextFactory contextFactory;
    @InjectMocks
    private SimpleTriggerStrategy subject;

    @Mock
    private ScheduledExecutorService executorService;
    @Mock
    private ScheduledContext context;
    @Mock
    private ExceptionHandler exceptionHandler;

    @Before
    public void setup() {
        when(this.contextFactory.createContext(any())).thenReturn(this.context);
    }

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
        this.subject.schedule(method, this, this.executorService, this.exceptionHandler);
    }

    @Test
    public void testSchedule() throws Exception {
        final Method method = getClass().getMethod("methodWithSimpleTrigger");

        final LockableRunnable runnable = mock(LockableRunnable.class);
        final ScheduledFuture future = mock(ScheduledFuture.class);

        when(this.executorService.scheduleWithFixedDelay(runnable, 12L, 5000L,
                TimeUnit.HOURS)).thenReturn(future);
        when(this.runnableBuilder.createLockedRunnableStack(any(),
                eq(this.context),
                eq(this.exceptionHandler))).thenReturn(runnable);

        this.subject.schedule(method, this, this.executorService, this.exceptionHandler);

        final InOrder order = inOrder(this.executorService, this.context, runnable);
        order.verify(this.executorService).scheduleWithFixedDelay(runnable, 12L, 5000L,
                TimeUnit.HOURS);
        order.verify(this.context).setFuture(future);
        order.verify(runnable).release();
    }
}
