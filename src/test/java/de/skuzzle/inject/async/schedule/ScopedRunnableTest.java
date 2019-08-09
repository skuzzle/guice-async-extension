package de.skuzzle.inject.async.schedule;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScopedRunnableTest {

    @Mock
    private Runnable wrapped;
    @Mock
    ScheduledContextImpl context;

    private Runnable subject;

    @Before
    public void setup() {
        this.subject = ScopedRunnable.of(this.wrapped, this.context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrappedNull() throws Exception {
        ScopedRunnable.of(null, this.context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContextNull() throws Exception {
        ScopedRunnable.of(this.wrapped, null);
    }

    @Test
    public void testRunScoped() throws Exception {
        this.subject.run();

        final InOrder order = inOrder(this.context, this.wrapped);
        order.verify(this.context).beginNewExecution();
        order.verify(this.wrapped).run();
        order.verify(this.context).finishExecution();
    }

    @Test(expected = RuntimeException.class)
    public void testRunScopedWithException() throws Exception {
        doThrow(RuntimeException.class).when(this.wrapped).run();

        this.subject.run();

        final InOrder order = inOrder(this.context, this.wrapped);
        order.verify(this.context).beginNewExecution();
        order.verify(this.wrapped).run();
        order.verify(this.context).finishExecution();
    }

}
