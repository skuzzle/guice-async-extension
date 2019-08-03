package de.skuzzle.inject.async.internal.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.skuzzle.inject.async.ExecutionContext;

public class ScheduledContextImplTest {

    private ScheduledContextImpl subject;

    public void justAMethod() {

    }

    @Before
    public void setup() throws NoSuchMethodException, SecurityException {
        if (ScheduledContextHolder.isContextActive()) {
            ScheduledContextHolder.pop();
        }

        final Method method = getClass().getMethod("justAMethod");
        this.subject = new ScheduledContextImpl(method, this);
    }

    @After
    public void cleanup() {
        if (ScheduledContextHolder.isContextActive()) {
            ScheduledContextHolder.pop();
        }
    }

    @Test
    public void testInitial() throws Exception {
        assertEquals(0, this.subject.getExecutionCount());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetExecutionNotActive() throws Exception {
        this.subject.getExecution();
    }

    @Test
    public void testBeginFinish() throws Exception {
        this.subject.beginNewExecution();

        final ExecutionContext ctx = this.subject.getExecution();
        assertEquals(0, ctx.getExecutionNr());
        assertEquals(1, this.subject.getExecutionCount());
        assertEquals(ctx.getMethod(), this.subject.getMethod());
        assertTrue(ScheduledContextHolder.isContextActive());
        assertEquals(this.subject, ScheduledContextHolder.getContext());

        this.subject.finishExecution();
        assertFalse(ScheduledContextHolder.isContextActive());
        assertEquals(1, this.subject.getExecutionCount());
    }

    @Test(expected = IllegalStateException.class)
    public void testCancelNoFuture() throws Exception {
        this.subject.cancel(false);
    }

    @Test(expected = IllegalStateException.class)
    public void testIsCancelledNoFuture() throws Exception {
        this.subject.isCancelled();
    }

    @Test
    public void testCancel() throws Exception {
        final Future<?> future = mock(Future.class);
        this.subject.setFuture(future);
        this.subject.cancel(true);
        verify(future).cancel(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFutureNull() throws Exception {
        this.subject.setFuture(null);
    }

    @Test
    public void testIsCancelled() throws Exception {
        final Future<?> future = mock(Future.class);
        when(future.isCancelled()).thenReturn(true);
        this.subject.setFuture(future);
        assertTrue(this.subject.isCancelled());
    }

}
