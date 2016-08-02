package de.skuzzle.inject.async.internal.runnables;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LatchLockableRunnableTest {

    @Mock
    private Runnable runnable;
    @InjectMocks
    private LatchLockableRunnable subject;

    @Test(timeout = 5000)
    public void testRunAfterRelease() throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(this.subject);
        Thread.sleep(250);
        verify(this.runnable, never()).run();
        this.subject.release();

        Thread.sleep(250);
        verify(this.runnable).run();
    }

    @Test(timeout = 5000)
    public void testRunInterrupt() throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(this.subject);

        Thread.sleep(250);
        executor.shutdownNow();
        verify(this.runnable, never()).run();
    }
}
