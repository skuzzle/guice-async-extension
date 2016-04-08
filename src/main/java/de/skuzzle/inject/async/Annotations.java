package de.skuzzle.inject.async;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.skuzzle.inject.async.annotation.Trigger;

class Annotations {

    public static Annotation findTriggerAnnotation(Method method) {
        Annotation result = null;
        for (final Annotation annotation : method.getAnnotations()) {
            if (isTriggerAnnotation(annotation)) {
                if (result == null) {
                    result = annotation;
                } else {
                    throw new IllegalStateException(String.format(
                            "Multiple @Trigger annotations found on '%s': %s, %s",
                            method, result, annotation));
                }
            }
        }
        if (result == null) {
            throw new IllegalStateException(String.format(
                    "No @Trigger annotation found on '%s'", method));
        }
        return result;
    }

    private static boolean isTriggerAnnotation(Annotation annotation) {
        final Class<?> type = annotation.annotationType();
        if (type.isAnnotationPresent(Trigger.class)) {
            return true;
        }
        return false;
    }
}
