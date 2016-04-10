package de.skuzzle.inject.async.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Named;

import org.junit.Test;

import com.google.inject.Key;

import de.skuzzle.inject.async.annotation.Executor;
import de.skuzzle.inject.async.annotation.Scheduler;
import de.skuzzle.inject.async.internal.Keys;

public class KeysTest {


    public void methodWithNoAnnotations() {

    }

    @Named("foo")
    public void methodWithBindingAnnoation() {

    }

    @Executor(ScheduledExecutorService.class)
    @Scheduler(ScheduledExecutorService.class)
    public void methodWithExecutorOnly() {

    }

    @com.google.inject.name.Named("xyz")
    @Executor(ScheduledExecutorService.class)
    public void methodWithBindingAndType() {

    }

    private void assertType(Class<?> type, Key<?> key) {
        assertEquals(type, key.getTypeLiteral().getRawType());
    }

    private void assertBindingAnnotation(
            Class<? extends Annotation> bindingAnnotationType, Key<?> key) {
        assertEquals(bindingAnnotationType, key.getAnnotationType());
    }

    private Method getMethod(String name) throws NoSuchMethodException, SecurityException {
        return getClass().getMethod(name);
    }

    @Test
    public void testFallBackDefault() throws Exception {
        final Method method = getMethod("methodWithNoAnnotations");
        final Key<? extends ExecutorService> key = Keys.getExecutorKey(method);
        assertType(ExecutorService.class, key);
        assertBindingAnnotation(DefaultBinding.class, key);
    }

    @Test
    public void testBindingAnnotationOnlyExecutor() throws Exception {
        final Method method = getMethod("methodWithBindingAnnoation");
        final Key<? extends ExecutorService> key = Keys.getExecutorKey(method);
        assertType(ExecutorService.class, key);
        assertBindingAnnotation(com.google.inject.name.Named.class, key);
    }

    @Test
    public void testBindingAnnotationOnlyScheduler() throws Exception {
        final Method method = getMethod("methodWithBindingAnnoation");
        final Key<? extends ScheduledExecutorService> key = Keys.getSchedulerKey(
                method);
        assertType(ScheduledExecutorService.class, key);
        assertBindingAnnotation(com.google.inject.name.Named.class, key);
    }

    @Test
    public void testTypeAndBindingAnnotationExecutor() throws Exception {
        final Method method = getMethod("methodWithBindingAndType");
        final Key<? extends ExecutorService> key = Keys.getExecutorKey(method);
        assertType(ScheduledExecutorService.class, key);
        assertBindingAnnotation(com.google.inject.name.Named.class, key);
    }

    @Test
    public void testTypeAndBindingAnnotationScheduler() throws Exception {
        final Method method = getMethod("methodWithBindingAndType");
        final Key<? extends ScheduledExecutorService> key = Keys.getSchedulerKey(
                method);
        assertType(ScheduledExecutorService.class, key);
        assertBindingAnnotation(com.google.inject.name.Named.class, key);
    }

    @Test
    public void testTypeOnlyExecutor() throws Exception {
        final Method method = getMethod("methodWithExecutorOnly");
        final Key<? extends ExecutorService> key = Keys.getExecutorKey(method);
        assertType(ScheduledExecutorService.class, key);
        assertBindingAnnotation(null, key);
    }

    @Test
    public void testTypeOnlyScheduler() throws Exception {
        final Method method = getMethod("methodWithExecutorOnly");
        final Key<? extends ScheduledExecutorService> key = Keys.getSchedulerKey(
                method);
        assertType(ScheduledExecutorService.class, key);
        assertBindingAnnotation(null, key);
    }

    @Test
    public void testPrivateCtor() throws Exception {
        final Constructor<Keys> ctor = Keys.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
}
