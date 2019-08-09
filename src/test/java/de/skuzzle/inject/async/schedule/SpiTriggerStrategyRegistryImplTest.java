package de.skuzzle.inject.async.schedule;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import de.skuzzle.inject.async.schedule.SpiTriggerStrategyRegistryImpl;
import de.skuzzle.inject.async.schedule.TriggerStrategy;
import de.skuzzle.inject.async.schedule.annotation.CronTrigger;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;
import de.skuzzle.inject.async.schedule.trigger.CronTriggerStrategy;

@RunWith(MockitoJUnitRunner.class)
public class SpiTriggerStrategyRegistryImplTest {

    @Mock
    private Injector injector;
    @Mock
    private Annotation triggerAnnotation;
    @InjectMocks
    private SpiTriggerStrategyRegistryImpl subject;

    @Before
    public void setup() {
        when(this.triggerAnnotation.annotationType()).thenReturn(
                (Class) CronTrigger.class);
    }

    @Test
    public void testGetStrategyFor() throws Exception {
        final TriggerStrategy strategy = this.subject
                .getStrategyFor(this.triggerAnnotation);
        verify(this.injector).injectMembers(Mockito.isA(CronTriggerStrategy.class));
        assertTrue(strategy instanceof CronTriggerStrategy);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetStrategyForUnknown() throws Exception {
        when(this.triggerAnnotation.annotationType()).thenReturn(
                (Class) Scheduled.class); // random annotation type
        this.subject.getStrategyFor(this.triggerAnnotation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStrategyforNull() throws Exception {
        this.subject.getStrategyFor(null);
    }
}
