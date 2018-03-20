package de.skuzzle.inject.async.internal.context;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.Guice;

public class ContextModuleTest {

    @Inject
    private ContextFactory contextFactory;

    @Test
    public void testInstall() throws Exception {
        Guice.createInjector(new ContextModule()).injectMembers(this);
        assertNotNull(this.contextFactory);
    }
}
