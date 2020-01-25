package de.skuzzle.inject.async.guice;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceAsyncServiceImplTest {

    @Test
    public void testShutdownNotAllFeaturesEnabled() throws Exception {
        final Injector injector = Guice.createInjector(GuiceAsync.createModuleWithFeatures(DefaultFeatures.ASYNC));
        final GuiceAsyncService asyncService = injector.getInstance(GuiceAsyncService.class);
        asyncService.shutdown(1, TimeUnit.SECONDS);
    }
}
