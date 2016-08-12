package de.skuzzle.inject.async.internal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.InjectionListener;

import de.skuzzle.inject.async.ExceptionHandler;
import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.OnError;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.annotation.Scheduler;

@RunWith(MockitoJUnitRunner.class)
public class SchedulingServiceImplTest {
    @Mock
    private Injector injector;
    @Mock
    private TriggerStrategyRegistry registry;
    @Captor
    private ArgumentCaptor<InjectionListener<SchedulerTypeListenerTest>> captor;
    @Mock
    private ScheduledExecutorService scheduler;
    @Mock
    private TriggerStrategy triggerStrategy;
    @Mock
    private ExceptionHandler exceptionHandler;

    private SchedulingServiceImpl subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new SchedulingServiceImpl(provider(this.injector),
                provider(this.registry));
        when(this.injector.getInstance(Key.get(ExceptionHandler.class)))
                .thenReturn(this.exceptionHandler);
        when(this.injector.getInstance(Key.get(ScheduledExecutorService.class)))
                .thenReturn(this.scheduler);
        when(this.registry.getStrategyFor(Mockito.any()))
                .thenReturn(this.triggerStrategy);
    }

    private static <T> Provider<T> provider(T t) {
        return () -> t;
    }

    @Scheduled
    @CronTrigger("* * * * * *")
    @Scheduler(ScheduledExecutorService.class)
    @OnError(ExceptionHandler.class)
    public void methodWithTrigger() {
    }

    @Scheduled
    @CronTrigger("* * * * * *")
    @Scheduler(ScheduledExecutorService.class)
    @OnError(ExceptionHandler.class)
    public static void staticMethodWithTrigger() {
    }

    public void methodWithouTrigger() {

    }

    @Test
    public void testNoTrigger() throws Exception {
        final Method expectedMethod = getClass().getMethod("methodWithouTrigger");

        this.subject.scheduleMemberMethod(expectedMethod, this);

        verifyNoMoreInteractions(this.triggerStrategy);
    }

    @Test
    public void testScheduleMemberMethod() throws Exception {
        final Method expectedMethod = getClass().getMethod("methodWithTrigger");

        this.subject.scheduleMemberMethod(expectedMethod, this);

        verify(this.triggerStrategy).schedule(expectedMethod, this, this.scheduler,
                this.exceptionHandler);
    }

    @Test
    public void testScheduleStaticMethod() throws Exception {
        final Method expectedMethod = getClass().getMethod("staticMethodWithTrigger");

        this.subject.scheduleStaticMethod(expectedMethod);

        verify(this.triggerStrategy).schedule(expectedMethod, null, this.scheduler,
                this.exceptionHandler);
    }
}
