package de.skuzzle.inject.async.internal.trigger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.annotation.DelayedTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.internal.context.ContextFactory;
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

    @Before
    public void setUp() throws Exception {
        when(this.contextFactory.createContext()).thenReturn(this.scheduledContext);
    }

    @Scheduled
    @DelayedTrigger(value = 5000, timeUnit = TimeUnit.DAYS)
    public void methodWithTrigger() {}

    public void methodWithoutTrigger() {}

    @Test(expected = IllegalArgumentException.class)
    public void testMissingTrigger() throws Exception {
        final Method method = getClass().getMethod("methodWithoutTrigger");
        this.subject.schedule(method, this, this.scheduler);
    }

    @Test
    public void testSchedule() throws Exception {
        final Method method = getClass().getMethod("methodWithTrigger");

        final Runnable invoke = mock(Runnable.class);
        final Runnable scoped = mock(Runnable.class);

        when(this.runnableBuilder.invoke(Mockito.any())).thenReturn(invoke);
        when(this.runnableBuilder.scope(invoke, this.scheduledContext)).thenReturn(scoped);

        this.subject.schedule(method, this, this.scheduler);
        verify(this.scheduler).schedule(scoped, 5000L, TimeUnit.DAYS);
    }
}
