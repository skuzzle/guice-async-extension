package de.skuzzle.inject.async.schedule;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

class SpiTriggerStrategyRegistryImpl implements TriggerStrategyRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(SpiTriggerStrategyRegistryImpl.class);

    private final Map<Class<? extends Annotation>, TriggerStrategy> strategies;

    private final Injector injector;

    @Inject
    public SpiTriggerStrategyRegistryImpl(Injector injector) {
        this.injector = injector;
        this.strategies = collectTriggerStrategies();
    }

    private Map<Class<? extends Annotation>, TriggerStrategy> collectTriggerStrategies() {
        final ServiceLoader<TriggerStrategy> services = ServiceLoader.load(
                TriggerStrategy.class);

        return asStream(services)
                .peek(strategy -> LOG.debug("Installing strategy '{}' to handle '{}'",
                        strategy, strategy.getTriggerType()))
                .peek(this.injector::injectMembers)
                .collect(Collectors.toMap(
                        TriggerStrategy::getTriggerType,
                        Function.identity()));
    }

    private static <T> Stream<T> asStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @Override
    public TriggerStrategy getStrategyFor(Annotation triggerAnnotation) {
        checkArgument(triggerAnnotation != null);
        final Class<? extends Annotation> type = triggerAnnotation.annotationType();
        final TriggerStrategy result = this.strategies.get(type);
        checkState(result != null,
                "There is no TriggerStrategy registered which is able to handle '%s'",
                type.getName());
        return result;
    }

}
