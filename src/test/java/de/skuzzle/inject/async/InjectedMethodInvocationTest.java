package de.skuzzle.inject.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class InjectedMethodInvocationTest {

    private Injector injector;
    private boolean invoked;
    private static boolean invokedStatic;

    @Before
    public void setUp() throws Exception {
        invokedStatic = false;
        this.invoked = false;
        this.injector = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(Integer.class).toInstance(5);
                bind(String.class).annotatedWith(Names.named("foo")).toInstance("foobar");
            }

        });
    }

    public void nonStaticMethod(Integer i, @Named("foo") String s) {
        assertEquals(5, i.intValue());
        assertEquals("foobar", s);
        this.invoked = true;
    }

    public static void staticMethod(Integer i, @Named("foo") String s) {
        assertEquals(5, i.intValue());
        assertEquals("foobar", s);
        invokedStatic = true;
    }


    @Test
    public void testCallNonStaticMethod() throws Throwable {
        final Method method = getClass().getMethod("nonStaticMethod",
                Integer.class, String.class);
        final MethodInvocation invocation = InjectedMethodInvocation.forMethod(method,
                this, this.injector);
        invocation.proceed();
        assertTrue(this.invoked);
    }

    @Test
    public void testCallStaticMethod() throws Throwable {
        final Method method = getClass().getMethod("staticMethod",
                Integer.class, String.class);
        final MethodInvocation invocation = InjectedMethodInvocation.forStatic(method,
                this.injector);
        invocation.proceed();
        assertTrue(invokedStatic);
    }

    @Test
    public void testCreateStatic() throws Exception {
        final Method method = getClass().getMethod("staticMethod",
                Integer.class, String.class);
        final MethodInvocation invocation = InjectedMethodInvocation.forStatic(method,
                this.injector);
        assertSame(method, invocation.getMethod());
        assertSame(method, invocation.getStaticPart());
        assertNull(invocation.getThis());
    }

    @Test
    public void testCreateNonStatic() throws Exception {
        final Method method = getClass().getMethod("nonStaticMethod",
                Integer.class, String.class);
        final MethodInvocation invocation = InjectedMethodInvocation.forMethod(method,
                this, this.injector);
        assertSame(method, invocation.getMethod());
        assertSame(method, invocation.getStaticPart());
        assertSame(this, invocation.getThis());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForMethodIllegalModifier() throws Exception {
        final Method method = getClass().getMethod("nonStaticMethod",
                Integer.class, String.class);
        InjectedMethodInvocation.forStatic(method, this.injector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForStaticNullInjector() throws Exception {
        final Method method = getClass().getMethod("staticMethod",
                Integer.class, String.class);
        InjectedMethodInvocation.forStatic(method, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForStaticNullMethod() throws Exception {
        InjectedMethodInvocation.forStatic(null, this.injector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForMethodNullMethod() throws Exception {
        InjectedMethodInvocation.forMethod(null, this, this.injector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForMethodNullInjector() throws Exception {
        final Method method = getClass().getMethod("nonStaticMethod",
                Integer.class, String.class);
        InjectedMethodInvocation.forMethod(method, this, null);
    }
}
