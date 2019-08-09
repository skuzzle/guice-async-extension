package de.skuzzle.inject.async.schedule;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.skuzzle.inject.async.schedule.ExceptionHandler;
import de.skuzzle.inject.async.schedule.LockableRunnable;
import de.skuzzle.inject.async.schedule.RunnableBuilderImpl;
import de.skuzzle.inject.async.schedule.ScheduledContext;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

@RunWith(MockitoJUnitRunner.class)
public class RunnableBuilderImplTest {

    @Mock
    private InjectedMethodInvocation invocation;
    @Mock
    private ScheduledContext context;
    @Mock
    private ExceptionHandler handler;

    private final RunnableBuilderImpl subject = new RunnableBuilderImpl();

    @Test
    public void testCreateLockedRunnable() throws Throwable {
        final LockableRunnable runnable = this.subject.createLockedRunnableStack(
                this.invocation, this.context, this.handler);

        runnable.release();
        runnable.run();

        final InOrder order = inOrder(this.invocation, this.context, this.handler);
        order.verify(this.context).beginNewExecution();
        order.verify(this.invocation).proceed();
        order.verify(this.context).finishExecution();
    }

    @Test
    public void testCreateRunnableStackException() throws Throwable {
        final RuntimeException ex = new RuntimeException();

        when(this.invocation.proceed()).thenThrow(ex);

        final Runnable runnable = this.subject.createLockedRunnableStack(this.invocation,
                this.context, this.handler);

        runnable.run();

        final InOrder order = inOrder(this.invocation, this.context, this.handler);
        order.verify(this.context).beginNewExecution();
        order.verify(this.invocation).proceed();
        order.verify(this.handler).onException(ex);
        order.verify(this.context).finishExecution();
    }

}
