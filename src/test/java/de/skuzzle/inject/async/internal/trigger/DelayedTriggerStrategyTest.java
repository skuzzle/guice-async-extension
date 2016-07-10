package de.skuzzle.inject.async.internal.trigger;

import static org.mockito.Mockito.verify;

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

import de.skuzzle.inject.async.annotation.DelayedTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.internal.runnables.RunnableBuilder;
import de.skuzzle.inject.async.internal.trigger.DelayedTriggerStrategy;

@RunWith(MockitoJUnitRunner.class)
public class DelayedTriggerStrategyTest {

    @Mock
    private Injector injector;
    @Mock
    private RunnableBuilder runnableBuilder;
    @Mock
    private ScheduledExecutorService scheduler;
    @InjectMocks
    private DelayedTriggerStrategy subject;

    @Before
    public void setUp() throws Exception {}

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
        this.subject.schedule(method, this, this.scheduler);
        verify(this.scheduler).schedule(Mockito.any(Runnable.class), Mockito.eq(5000L),
                Mockito.eq(TimeUnit.DAYS));
    }
}
