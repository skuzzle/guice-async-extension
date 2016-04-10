package de.skuzzle.inject.async.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Allows to iterate the {@link Method methods} of a class and its whole type
 * hierarchy.
 *
 * @author Simon Taddiken
 */
public final class MethodVisitor {

    private MethodVisitor() {
        // hidden ctor
    }

    /**
     * Starting at given type, traverses all super types (except Object.class)
     * and passes each encountered method (including private and static ones) to
     * the given consumer.
     *
     * @param type The type to start traversal.
     * @param action Action to execute for each encountered method.
     */
    public static void forEachMemberMethod(Class<?> type, Consumer<Method> action) {
        if (type == Object.class) {
            return;
        } else {
            forEachMemberMethod(type.getSuperclass(), action);
        }
        Arrays.stream(type.getDeclaredMethods())
                .filter(MethodVisitor::notStatic)
                .forEach(action);
    }

    private static boolean notStatic(Method method) {
        return !Modifier.isStatic(method.getModifiers());
    }
}
