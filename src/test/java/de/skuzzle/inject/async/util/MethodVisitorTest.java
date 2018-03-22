package de.skuzzle.inject.async.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class MethodVisitorTest {

    public static class SuperClass {

        public static void staticSuperMethod() {

        }

        public void publicSuperMethod() {

        }

        private void privateSuperMethod() {

        }
    }

    public static class SubClass extends SuperClass {
        public void pulicMethod() {

        }

        private void privateMethod() {

        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testVisitAll() throws Exception {
        final Set<Method> visited = new HashSet<>();
        MethodVisitor.forEachMemberMethod(SubClass.class, visited::add);
        assertEquals(4, visited.size());
    }

    @Test
    public void testVisitAllObject() throws Exception {
        final Set<Method> visited = new HashSet<>();
        MethodVisitor.forEachMemberMethod(Object.class, visited::add);
        assertEquals(0, visited.size());
    }

    @Test
    public void testPrivateCtor() throws Exception {
        final Constructor<MethodVisitor> ctor = MethodVisitor.class
                .getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }

    @Test
    public void testVisitStatic() throws Exception {
        final Set<Method> visited = new HashSet<>();
        MethodVisitor.forEachStaticMethod(SubClass.class, visited::add);
        assertEquals(1, visited.size());
    }
}
