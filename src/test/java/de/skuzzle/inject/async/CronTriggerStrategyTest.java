package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import de.skuzzle.inject.async.annotation.CronTrigger;

@RunWith(MockitoJUnitRunner.class)
public class CronTriggerStrategyTest {

    @Mock
    private Injector injector;
    @InjectMocks
    private CronTriggerStrategy subject;

    @Mock
    private ScheduledExecutorService executor;

    @Before
    public void setUp() throws Exception {}

    @CronTrigger("0 0 0 * * *")
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
        this.subject.schedule(method, this, this.executor);
    }

    @Test
    public void testSchedule() throws Exception {
        final Method method = getClass().getMethod("scheduledMethod");
        this.subject.schedule(method, this, this.executor);
        verify(this.executor).execute(Mockito.isA(ReScheduleRunnable.class));
    }
}
