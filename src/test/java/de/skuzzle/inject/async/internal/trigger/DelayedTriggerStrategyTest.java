package de.skuzzle.inject.async.internal.trigger;

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

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.annotation.DelayedTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.internal.context.ContextFactory;
import de.skuzzle.inject.async.internal.runnables.LockableRunnable;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;

@RunWith(MockitoJUnitRunner.class)
public class DelayedTriggerStrategyTest {

    @Mock
    private Injector injector;
    @Mock
    private RunnableBuilder runnableBuilder;
    @Mock
    private ScheduledExecutorService scheduler;
    @Mock
    private ContextFactory contextFactory;
    @InjectMocks
    private DelayedTriggerStrategy subject;

    @Mock
    private ScheduledContext scheduledContext;
    @Mock
    private ExceptionHandler exceptionHandler;

    @Before
    public void setUp() throws Exception {
        when(this.contextFactory.createContext(any())).thenReturn(this.scheduledContext);
    }

    @Scheduled
    @DelayedTrigger(value = 5000, timeUnit = TimeUnit.DAYS)
    public void methodWithTrigger() {
    }

    public void methodWithoutTrigger() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingTrigger() throws Exception {
        final Method method = getClass().getMethod("methodWithoutTrigger");
        this.subject.schedule(method, this, this.scheduler, this.exceptionHandler);
    }

    @Test
    public void testSchedule() throws Exception {
        final Method method = getClass().getMethod("methodWithTrigger");

        final LockableRunnable runnable = mock(LockableRunnable.class);
        final ScheduledFuture future = mock(ScheduledFuture.class);

        when(this.scheduler.schedule(runnable, 5000L, TimeUnit.DAYS)).thenReturn(future);
        when(this.runnableBuilder.createLockedRunnableStack(any(),
                eq(this.scheduledContext),
                eq(this.exceptionHandler))).thenReturn(runnable);

        this.subject.schedule(method, this, this.scheduler, this.exceptionHandler);

        final InOrder order = inOrder(this.scheduler, this.scheduledContext, runnable);
        order.verify(this.scheduler).schedule(runnable, 5000L, TimeUnit.DAYS);
        order.verify(this.scheduledContext).setFuture(future);
        order.verify(runnable).release();
    }
}
