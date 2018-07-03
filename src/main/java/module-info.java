
/**
 * @author Simon Taddiken
 */
module de.skuzzle.inject.async {
    exports de.skuzzle.inject.async.util;
    exports de.skuzzle.inject.async;
    exports de.skuzzle.inject.async.annotation;

    requires aopalliance;
    requires cron.utils;
    requires guava;
    requires transitive guice;
    requires guice.scoped.proxy.extension;
    requires javax.inject;
    requires slf4j.api;
}