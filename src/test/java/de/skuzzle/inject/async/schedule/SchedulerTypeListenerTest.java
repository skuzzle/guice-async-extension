package de.skuzzle.inject.async.schedule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;

import de.skuzzle.inject.async.schedule.ExceptionHandler;
import de.skuzzle.inject.async.schedule.SchedulerTypeListener;
import de.skuzzle.inject.async.schedule.SchedulingService;
import de.skuzzle.inject.async.schedule.annotation.CronTrigger;
import de.skuzzle.inject.async.schedule.annotation.OnError;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;
import de.skuzzle.inject.async.schedule.annotation.Scheduler;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerTypeListenerTest {

    @Mock
    private TypeEncounter<SchedulerTypeListenerTest> encounter;
    @Mock
    private Injector injector;
    @Mock
    private TriggerStrategyRegistry registry;
    @Captor
    private ArgumentCaptor<InjectionListener<SchedulerTypeListenerTest>> captor;
    @Mock
    private ScheduledExecutorService scheduler;
    @Mock
    private ExceptionHandler exceptionHandler;
    @Mock
    private SchedulingService schedulingService;

    @Before
    public void setUp() throws Exception {
        when(this.encounter.getProvider(Injector.class))
                .thenReturn(provider(this.injector));
        when(this.encounter.getProvider(TriggerStrategyRegistry.class))
                .thenReturn(provider(this.registry));
        when(this.injector.getInstance(Key.get(ExceptionHandler.class)))
                .thenReturn(this.exceptionHandler);
        when(this.injector.getInstance(Key.get(ScheduledExecutorService.class)))
                .thenReturn(this.scheduler);
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

    @Test
    public void testHear() throws Exception {
        final Method expectedMethod = getClass().getMethod("methodWithTrigger");
        final Method expectedStaticMethod = getClass()
                .getMethod("staticMethodWithTrigger");
        final TypeLiteral<SchedulerTypeListenerTest> type = new TypeLiteral<SchedulerTypeListenerTest>() {};
        final SchedulerTypeListener subject = new SchedulerTypeListener(
                this.schedulingService);

        subject.hear(type, this.encounter);
        verify(this.encounter).register(this.captor.capture());
        final InjectionListener<SchedulerTypeListenerTest> listener = this.captor
                .getValue();

        listener.afterInjection(this);
        verify(this.schedulingService).scheduleMemberMethod(expectedMethod, this);

        subject.injectorReady();
        verify(this.schedulingService).scheduleStaticMethod(expectedStaticMethod);
    }
}
