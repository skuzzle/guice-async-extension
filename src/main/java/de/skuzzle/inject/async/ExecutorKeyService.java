package de.skuzzle.inject.async;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

import com.google.inject.Key;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;

class ExecutorKeyService {

    /** Fall back key if the user did not bind any executor service */
    private static final Key<? extends ExecutorService> DEFAULT_KEY = Key.get(
            ExecutorService.class, DefaultExecutor.class);

    /**
     * Finds the key of the {@link ExecutorService} to use to execute the given method.
     *
     * @param method The method to find the key for.
     * @return The ExecutorService key.
     * @throws ErrorsException If method has inconsistent annotations.
     */
    public Key<? extends ExecutorService> getKey(Method method) throws ErrorsException {
        final Class<? extends ExecutorService> type;
        boolean executorSpecified = false;
        if (method.isAnnotationPresent(Executor.class)) {
            type = method.getAnnotation(Executor.class).value();
            executorSpecified = true;
        } else {
            type = ExecutorService.class;
        }

        final Errors errors = new Errors(method);
        final Annotation bindingAnnotation = Annotations.findBindingAnnotation(errors,
                method, method.getAnnotations());
        errors.throwIfNewErrors(0);
        final Key<? extends ExecutorService> key;
        if (bindingAnnotation == null && executorSpecified) {
            key = Key.get(type);
        } else if (bindingAnnotation == null && !executorSpecified) {
            key = DEFAULT_KEY;
        } else {
            key = Key.get(type, bindingAnnotation);
        }
        return key;
    }
}
