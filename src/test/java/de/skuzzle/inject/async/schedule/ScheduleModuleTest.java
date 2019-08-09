package de.skuzzle.inject.async.schedule;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.Guice;

import de.skuzzle.inject.async.schedule.ContextFactory;
import de.skuzzle.inject.async.schedule.ScheduleModule;

public class ScheduleModuleTest {

    @Inject
    private ContextFactory contextFactory;

    @Test
    public void testInstall() throws Exception {
        Guice.createInjector(new ScheduleModule()).injectMembers(this);
        assertNotNull(this.contextFactory);
    }
}
