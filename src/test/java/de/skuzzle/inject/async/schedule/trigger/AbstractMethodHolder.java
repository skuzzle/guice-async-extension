package de.skuzzle.inject.async.schedule.trigger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.skuzzle.inject.async.schedule.ScheduledContext;

abstract class AbstractMethodHolder implements Module {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMethodHolder.class);

    protected final CountDownLatch throwingException = new CountDownLatch(1);
    protected final CountDownLatch threeTimes = new CountDownLatch(3);
    protected final CountDownLatch cancel = new CountDownLatch(4);
    protected final MockExceptionHandler exceptionHandler = new MockExceptionHandler(throwingException);

    @Override
    public void configure(Binder binder) {
        binder.bind((Class) this.getClass()).toInstance(this);
        binder.bind(MockExceptionHandler.class).toInstance(exceptionHandler);
    }

    protected void throwingException() {
        throw new RuntimeException("This exception is expected to be thrown!");
    }

    protected void threeTimes() {
        threeTimes.countDown();
    }

    protected void cancel(ScheduledContext ctx) {
        ctx.cancel(true);
        cancel.countDown();
    }

    protected void waitFor(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(15, TimeUnit.SECONDS)) {
            throw new AssertionError("Expected latch to count down to zero within 15 seconds");
        }
    }

    protected void expectTimeoutFor(CountDownLatch latch) throws InterruptedException {
        if (latch.await(15, TimeUnit.SECONDS)) {
            throw new AssertionError("Expected latch to time out within 15 seconds");
        }
    }

}
