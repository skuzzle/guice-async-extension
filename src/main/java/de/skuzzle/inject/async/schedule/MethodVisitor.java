package de.skuzzle.inject.async.schedule;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Allows to iterate the {@link Method methods} of a class and its whole type hierarchy.
 *
 * @author Simon Taddiken
 */
public final class MethodVisitor {

    // HACK: during maven compilation, jacoco agents adds a private static method to all
    // classes which disturbs the unit tests (that are counting the visited static
    // methods). Thus we need to filter out that particular method.
    private static final String JACOCO_INIT = "$jacocoInit";

    private MethodVisitor() {
        // hidden ctor
    }

    /**
     * Starting at given type, traverses all super types (except Object.class) and passes
     * each encountered static method (including private ones) to the given consumer.
     *
     * @param type The type to start traversal.
     * @param action Action to execute for each encountered method.
     * @since 0.4.0
     */
    public static void forEachStaticMethod(Class<?> type, Consumer<Method> action) {
        forEachMethod(type, action, MethodVisitor::isStatic);
    }

    /**
     * Starting at given type, traverses all super types (except Object.class) and passes
     * each encountered member method (including private ones) to the given consumer.
     *
     * @param type The type to start traversal.
     * @param action Action to execute for each encountered method.
     */
    public static void forEachMemberMethod(Class<?> type, Consumer<Method> action) {
        forEachMethod(type, action, MethodVisitor::notStatic);
    }

    private static void forEachMethod(Class<?> type, Consumer<Method> action,
            Predicate<Method> filter) {
        if (type == Object.class) {
            return;
        } else {
            forEachMethod(type.getSuperclass(), action, filter);
        }
        Arrays.stream(type.getDeclaredMethods())
                .filter(filter)
                .forEach(action);
    }

    private static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers())
                && !JACOCO_INIT.equals(method.getName());
    }

    private static boolean notStatic(Method method) {
        return !Modifier.isStatic(method.getModifiers());
    }
}
