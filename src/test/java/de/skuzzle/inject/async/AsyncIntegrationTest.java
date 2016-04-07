package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

import de.skuzzle.inject.async.annotation.Async;

public class AsyncIntegrationTest {

    public static class InjectMe {

        @Async
        @Named("sampleExecutor")
        public void asyncMethodWithVoidReturnType(String[] arg) throws InterruptedException {
            arg[0] = "result";
            Thread.sleep(2000);
        }

        @Async
        @Named("sampleExecutor")
        public Future<String> asyncMethodWithFutureReturnType() throws InterruptedException {
            final String result = "result";
            Thread.sleep(2000);
            return Futures.delegate(result);
        }

        @Async
        public void asyncMethodForDefaultExecutor(String[] arg) throws InterruptedException {
            arg[0] = "result";
            Thread.sleep(2000);
        }
    }

    public static class TestModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(InjectMe.class).in(Singleton.class);
        }

        @Provides
        @Named("sampleExecutor")
        public ExecutorService provideExecutor() {
            return Executors.newCachedThreadPool();
        }
    }

    @Inject
    private InjectMe injectMe;

    @Before
    public void setup() {
        Guice.createInjector(new TestModule(), new AsyncModule()).injectMembers(this);
    }

    @Test
    public void testVoid() throws Exception {
        final String[] arg = new String[1];
        this.injectMe.asyncMethodWithVoidReturnType(arg);
        assertNull(arg[0]);
        Thread.sleep(3000);
        assertEquals("result", arg[0]);
    }

    @Test
    public void testVoidWithDefaultExecutor() throws Exception {
        final String[] arg = new String[1];
        this.injectMe.asyncMethodForDefaultExecutor(arg);
        assertNull(arg[0]);
        Thread.sleep(3000);
        assertEquals("result", arg[0]);
    }

    @Test
    public void testFuture() throws Exception {
        final Future<String> future = this.injectMe.asyncMethodWithFutureReturnType();
        assertEquals("result", future.get());
    }
}
