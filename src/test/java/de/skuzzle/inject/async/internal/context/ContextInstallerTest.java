package de.skuzzle.inject.async.internal.context;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

public class ContextInstallerTest {

    @Inject
    private ContextFactory contextFactory;

    @Test
    public void testInstall() throws Exception {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                ContextInstaller.install(binder());
            }
        }).injectMembers(this);
        ;

    }
}
