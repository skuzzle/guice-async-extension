package de.skuzzle.inject.async.internal;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import de.skuzzle.inject.async.ScheduledContext;
import de.skuzzle.inject.async.internal.context.ScheduledContextHolder;

public class DefaultExceptionHandlerTest {

    private final DefaultExceptionHandler subject = new DefaultExceptionHandler();

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
