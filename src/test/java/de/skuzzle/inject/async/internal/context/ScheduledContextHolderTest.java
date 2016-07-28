package de.skuzzle.inject.async.internal.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.skuzzle.inject.async.ScheduledContext;

public class ScheduledContextHolderTest {

    @Before
    public void setup() {
        if (ScheduledContextHolder.isContextActive()) {
            ScheduledContextHolder.pop();
        }
    }

    @After
    public void after() {
        if (ScheduledContextHolder.isContextActive()) {
            ScheduledContextHolder.pop();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testPopNotPresent() throws Exception {
        ScheduledContextHolder.pop();
    }

    @Test
    public void testPushPop() throws Exception {
        final ScheduledContext ctx = mock(ScheduledContext.class);
        ScheduledContextHolder.push(ctx);
        assertTrue(ScheduledContextHolder.isContextActive());
        assertEquals(ctx, ScheduledContextHolder.getContext());
        ScheduledContextHolder.pop();
        assertFalse(ScheduledContextHolder.isContextActive());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContextNotActive() throws Exception {
        ScheduledContextHolder.getContext();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPushNull() throws Exception {
        ScheduledContextHolder.push(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testPushWhileActive() throws Exception {
        final ScheduledContext ctx = mock(ScheduledContext.class);
        ScheduledContextHolder.push(ctx);
        ScheduledContextHolder.push(ctx);
    }
}
