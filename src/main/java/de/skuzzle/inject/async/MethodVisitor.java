package de.skuzzle.inject.async;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

class MethodVisitor {

    private MethodVisitor() {
        // hidden ctor
    }

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
