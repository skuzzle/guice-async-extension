package de.skuzzle.inject.async.schedule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.skuzzle.inject.async.annotation.Trigger;

final class Annotations {

    private Annotations() {
        // hidden ctor
    }

    /**
     * Searches the annotations of the provided method for an annotation type that itself
     * is annotated with {@link Trigger}. An exception is thrown if the method does not
     * have a unique Trigger annotation.
     *
     * @param method The method to search.
     * @return The unique trigger annotation.
     */
    public static Annotation findTriggerAnnotation(Method method) {
        Annotation result = null;
        for (final Annotation annotation : method.getAnnotations()) {
            if (isTriggerAnnotation(annotation)) {
                if (result == null) {
                    result = annotation;
                } else {
                    throw new IllegalStateException(String.format("Multiple @Trigger annotations found on '%s': %s, %s",
                            method, result, annotation));
                }
            }
        }
        if (result == null) {
            throw new IllegalStateException(String.format("No @Trigger annotation found on '%s'", method));
        }
        return result;
    }

    private static boolean isTriggerAnnotation(Annotation annotation) {
        final Class<?> type = annotation.annotationType();
        return type.isAnnotationPresent(Trigger.class);
    }
}
