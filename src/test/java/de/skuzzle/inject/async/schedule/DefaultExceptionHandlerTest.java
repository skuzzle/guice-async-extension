package de.skuzzle.inject.async.schedule;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import de.skuzzle.inject.async.schedule.DefaultExceptionHandler;
import de.skuzzle.inject.async.schedule.ScheduledContext;

public class DefaultExceptionHandlerTest {

    private final DefaultExceptionHandler subject = new DefaultExceptionHandler();

    @Before
    public void setup() {
        if (ScheduledContextHolder.isContextActive()) {
            ScheduledContextHolder.pop();
        }
    }

    @Test
    public void testHandleWithContext() throws Exception {
        ScheduledContextHolder.push(mock(ScheduledContext.class));
        this.subject.onException(new Exception());
    }

    @Test
    public void testHandleWithoutContext() throws Exception {
        this.subject.onException(new Exception());
    }
}
