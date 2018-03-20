package de.skuzzle.inject.async.internal.runnables;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.Guice;

public class RunnablesModuleTest {

    @Inject
    private RunnableBuilder runnableBuilder;

    @Test
    public void testInstall() throws Exception {
        Guice.createInjector(new RunnablesModule()).injectMembers(this);
        assertNotNull(this.runnableBuilder);
    }
}
