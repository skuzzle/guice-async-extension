package de.skuzzle.inject.async.internal;

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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import de.skuzzle.inject.async.TriggerStrategy;
import de.skuzzle.inject.async.TriggerStrategyRegistry;
import de.skuzzle.inject.async.annotation.CronTrigger;
import de.skuzzle.inject.async.annotation.Scheduled;
import de.skuzzle.inject.async.annotation.Scheduler;

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
    private TriggerStrategy triggerStrategy;

    @Before
    public void setUp() throws Exception {
        when(this.encounter.getProvider(Injector.class))
                .thenReturn(provider(this.injector));
        when(this.encounter.getProvider(TriggerStrategyRegistry.class))
                .thenReturn(provider(this.registry));
        when(this.injector.getInstance(Key.get(ScheduledExecutorService.class)))
                .thenReturn(this.scheduler);
        when(this.registry.getStrategyFor(Mockito.any())).thenReturn(this.triggerStrategy);
    }

    private static <T> Provider<T> provider(T t) {
        return () -> t;
    }

    @Scheduled
    @CronTrigger("* * * * * *")
    @Scheduler(ScheduledExecutorService.class)
    public void methodWithTrigger() {
    }

    @Test
    public void testHear() throws Exception {
        final Method expectedMethod = getClass().getMethod("methodWithTrigger");
        final TypeLiteral<SchedulerTypeListenerTest> type =
                new TypeLiteral<SchedulerTypeListenerTest>() {};
        final TypeListener subject = new SchedulerTypeListener();

        subject.hear(type, this.encounter);
        verify(this.encounter).register(this.captor.capture());
        final InjectionListener<SchedulerTypeListenerTest> listener =
                this.captor.getValue();

        listener.afterInjection(this);
        verify(this.triggerStrategy).schedule(expectedMethod, this, this.scheduler);
    }
}
