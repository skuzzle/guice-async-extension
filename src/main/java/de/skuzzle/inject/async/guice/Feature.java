package de.skuzzle.inject.async.guice;

import java.util.concurrent.TimeUnit;

import com.google.inject.Binder;
import com.google.inject.Injector;

/**
 * A stand alone feature that can be passed to {@link GuiceAsync}. Use
 * {@link DefaultFeatures} or the dedicated {@link ScheduleFeature} which allows
 * customization.
 *
 * @author Simon Taddiken
 * @since 2.0.0
 */
public interface Feature {

    /**
     * Installs the modules relevant to this feature to the given {@link Binder}.
     *
     * @param binder The binder to install any required modules to.
     * @param principal The {@link GuiceAsync} instance guarding the modules from
     *            unintended instantiation.
     */
    void installModuleTo(Binder binder, GuiceAsync principal);

    /**
     * Makes sure to shutdown this feature's executor when
     * {@link GuiceAsyncService#shutdown(long, TimeUnit)} is being called.
     *
     * @param injector The injector.
     * @param timeout The time to wait for an orderly shutdown.
     * @param timeUnit Unit for the timeout parameter.
     * @return Whether the executor orderly shutdown within given time.
     */
    boolean cleanupExecutor(Injector injector, long timeout, TimeUnit timeUnit);
}
