package de.skuzzle.inject.async.internal.runnables;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.skuzzle.inject.async.ExceptionHandler;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlingRunnableTest {

    @Mock
    private Runnable wrapped;
    @Mock
    private ExceptionHandler handler;
    @InjectMocks
    private ExceptionHandlingRunnable subject;

    @Test
    public void testRunException() throws Exception {
        final RuntimeException ex = new RuntimeException();
        doThrow(ex).when(this.wrapped).run();
        this.subject.run();
        verify(this.handler).onException(ex);
    }

    @Test
    public void testRunNoException() throws Exception {
        this.subject.run();
        verify(this.wrapped).run();
        verifyZeroInteractions(this.handler);
    }
}
