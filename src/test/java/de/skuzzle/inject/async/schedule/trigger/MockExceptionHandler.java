package de.skuzzle.inject.async.schedule.trigger;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CountDownLatch;

import org.mockito.Mockito;

import de.skuzzle.inject.async.schedule.ExceptionHandler;

class MockExceptionHandler implements ExceptionHandler {

    private final ExceptionHandler mock = Mockito.mock(ExceptionHandler.class);
    private final CountDownLatch latch;

    MockExceptionHandler(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onException(Exception e) {
        mock.onException(e);
        latch.countDown();
    }

    public void assertExceptionThrown(Class<? extends Exception> exceptionType) {
        verify(mock).onException(isA(exceptionType));
    }

}
