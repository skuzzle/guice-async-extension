package de.skuzzle.inject.async.guice;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import de.skuzzle.inject.async.methods.annotation.Async;
import de.skuzzle.inject.async.schedule.annotation.Scheduled;

/**
 * Entry point for enabling asynchronous method support within your guice application.
 *
 * <pre>
 * public class MyModule extends AbstractModule {
 *
 *     &#64;Override
 *     public void configure() {
 *         GuiceAsync.enableFor(binder());
 *         // ...
 *     }
 * }
 * </pre>
 * <p>
 * You may choose to only enable scheduling OR async methods in case you do not need both.
 * See {@link #enableFeaturesFor(Binder, Feature...)} and
 * {@link #createModuleWithFeatures(Feature...)}.
 * <p>
 * Please see the JavaDoc of the {@link Async} and {@link Scheduled} annotation for
 * further usage information.
 *
 * @author Simon Taddiken
 * @see Async
 * @see Scheduled
 */
public final class GuiceAsync {

    private GuiceAsync() {
        // hidden constructor
    }

    /**
     * Enable support for the {@link Async} annotation in classes that are used with the
     * injector that will be created from the given {@link Binder}.
     *
     * @param binder The binder to register with.
     */
    public static void enableFor(Binder binder) {
        enableFeaturesFor(binder, DefaultFeatures.ASYNC, DefaultFeatures.SCHEDULE);
    }

    /**
     * Enable support for the given {@link DefaultFeatures features}. Allows to separately
     * enable support for async or scheduled.
     *
     * @param binder The binder to register with.
     * @param features The features to enable.
     * @since 2.0.0
     * @see DefaultFeatures
     */
    public static void enableFeaturesFor(Binder binder, Feature... features) {
        checkArgument(binder != null, "binder must not be null");
        binder.install(createModuleWithFeatures(features));
    }

    /**
     * Creates a module that can be used to enable asynchronous method and scheduling
     * support.
     *
     * @return A module that exposes all bindings needed for asynchronous method support.
     * @since 0.2.0
     */
    public static Module createModule() {
        return createModuleWithFeatures(DefaultFeatures.ASYNC, DefaultFeatures.SCHEDULE);
    }

    /**
     * Creates a module that can be used to enable the given features.
     *
     * @param features The features to enable.
     * @return The module.
     * @since 2.0.0
     */
    public static Module createModuleWithFeatures(Feature... features) {
        final GuiceAsync principal = new GuiceAsync();
        final Set<Feature> featureSet = ImmutableSet.copyOf(features);
        return new GuiceAsyncModule(principal, featureSet);
    }

    private static final class GuiceAsyncModule extends AbstractModule {

        private final GuiceAsync principal;
        private final Set<Feature> enabledFeatures;

        public GuiceAsyncModule(GuiceAsync principal, Set<Feature> features) {
            checkArgument(!features.isEmpty(), "Set of features must not be empty");
            this.principal = principal;
            this.enabledFeatures = features;
        }

        @Override
        protected void configure() {
            enabledFeatures.forEach(feature -> feature.installModuleTo(binder(), principal));
            bind(GuiceAsyncService.class).to(GuiceAsyncServiceImpl.class).in(Singleton.class);
        }

        @Provides
        @Singleton
        @DefaultBinding
        Set<Feature> provideFeatures() {
            return enabledFeatures;
        }

        @Provides
        @Singleton
        @DefaultBinding
        ThreadFactory provideThreadFactory() {
            return new ThreadFactoryBuilder()
                    .setNameFormat("guice-async-%d")
                    .build();
        }

        @Override
        public int hashCode() {
            return 31;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof GuiceAsyncModule;
        }
    }
}
