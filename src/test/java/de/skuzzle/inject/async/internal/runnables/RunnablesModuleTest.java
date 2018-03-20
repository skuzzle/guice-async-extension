package de.skuzzle.inject.async.internal.runnables;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

public class RunnablesInstallerTest {

    @Inject
    private RunnableBuilder runnableBuilder;

    @Test
    public void testInstall() throws Exception {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                RunnablesInstaller.install(binder());
            }
        });
    }
}
