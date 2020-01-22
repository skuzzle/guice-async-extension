package de.skuzzle.inject.async.guice;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.inject.Injector;

class GuiceAsyncServiceImpl implements GuiceAsyncService {

    private final Injector injector;
    private final Set<Feature> features;

    @Inject
    public GuiceAsyncServiceImpl(Injector injector, @DefaultBinding Set<Feature> features) {
        this.injector = injector;
        this.features = features;
    }

    @Override
    public boolean shutdown(long timeout, TimeUnit timeUnit) {
        return features.stream()
                .map(feature -> feature.cleanupExecutor(injector, timeout, timeUnit))
                .reduce(Boolean.TRUE, Boolean::logicalAnd);
    }

}
