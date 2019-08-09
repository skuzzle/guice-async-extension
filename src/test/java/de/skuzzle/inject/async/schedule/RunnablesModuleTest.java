package de.skuzzle.inject.async.schedule;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.Guice;

import de.skuzzle.inject.async.schedule.RunnableBuilder;
import de.skuzzle.inject.async.schedule.RunnablesModule;

public class RunnablesModuleTest {

    @Inject
    private RunnableBuilder runnableBuilder;

    @Test
    public void testInstall() throws Exception {
        Guice.createInjector(new RunnablesModule()).injectMembers(this);
        assertNotNull(this.runnableBuilder);
    }
}
