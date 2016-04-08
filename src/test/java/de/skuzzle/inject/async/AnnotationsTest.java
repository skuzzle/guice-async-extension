package de.skuzzle.inject.async;

import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import de.skuzzle.inject.async.annotation.Trigger;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationsTest {


    @Retention(RetentionPolicy.RUNTIME)
    @Trigger
    public @interface SampleTrigger {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Trigger
    public @interface SecondTrigger {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SomeAnnotation {

    }

    @InjectMocks
    private Annotations subject;

    @Before
    public void setUp() throws Exception {}

    @SomeAnnotation
    @SampleTrigger
    public void withTrigger() {

    }

    @SampleTrigger
    @SecondTrigger
    public void withMultipleTriggers() {

    }

    public void withoutTrigger() {

    }

    @Test
    public void testFindTriggerAnnotation() throws Exception {
        final Method method = getClass().getMethod("withTrigger");
        final Annotation result = this.subject.findTriggerAnnotation(method);
        assertTrue(result instanceof SampleTrigger);
    }

    @Test(expected = IllegalStateException.class)
    public void testFindTriggerAnnotationDuplicate() throws Exception {
        final Method method = getClass().getMethod("withMultipleTriggers");
        this.subject.findTriggerAnnotation(method);
    }

    @Test(expected = IllegalStateException.class)
    public void testFindTriggerAnnotationNoAnnotation() throws Exception {
        final Method method = getClass().getMethod("withoutTrigger");
        this.subject.findTriggerAnnotation(method);
    }
}
