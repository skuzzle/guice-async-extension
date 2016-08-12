package de.skuzzle.inject.async.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;

/**
 * Allows to invoke any method without explicitly providing its arguments. Arguments are
 * looked up by creating {@link Key} objects from the method's parameter types and then
 * passing them to an {@link Injector} to retrieve the actual argument.
 * <p>
 * A {@link BindingAnnotation} may optionally be present on an argument type to refine the
 * Key that is created to look up the parameter's actual value.
 * </p>
 *
 * @author Simon Taddiken
 */
public class InjectedMethodInvocation {

    private final Injector injector;
    private final Object self;
    private final Method method;

    private InjectedMethodInvocation(Injector injector, Object self, Method method) {
        this.injector = injector;
        this.self = self;
        this.method = method;
    }

    /**
     * Creates an {@linkplain InjectedMethodInvocation} object that is able to invoke the
     * provided static method. The actual parameters for the invocation will be looked up
     * using the given injector.
     * <p>
     * You can call {@link InjectedMethodInvocation#proceed()} to invoke the method.
     * </p>
     *
     * @param method A static method.
     * @param injector The injector which is queried for actual method arguments.
     * @return The MethodInvocation.
     */
    public static InjectedMethodInvocation forStatic(Method method, Injector injector) {
        checkArgument(method != null);
        checkArgument(Modifier.isStatic(method.getModifiers()));
        checkArgument(injector != null);
        return new InjectedMethodInvocation(injector, null, method);
    }

    /**
     * Creates a {@linkplain InjectedMethodInvocation} object that is able to invoke the
     * provided method. The actual parameters for the invocation will be looked up using
     * the given injector. This method supports static methods by leaving the self
     * parameter null.
     * <p>
     * You can call {@link InjectedMethodInvocation#proceed()} to invoke the method.
     * </p>
     *
     * @param method A method.
     * @param self The object on which the method will be called. May be null for invoking
     *            static methods.
     * @param injector The injector which is queried for actual method arguments.
     * @return The MethodInvocation.
     */
    public static InjectedMethodInvocation forMethod(Method method, Object self,
            Injector injector) {
        checkArgument(method != null, "Method must not be null");
        checkArgument(self != null ^ Modifier.isStatic(method.getModifiers()),
                "Method must either be static or a reference object must be passed");
        checkArgument(injector != null, "Injector must not be null");
        return new InjectedMethodInvocation(injector, self, method);
    }

    private Object[] getArguments() {
        final Object[] result = new Object[this.method.getParameterCount()];
        final Errors errors = new Errors(this.method);
        for (int i = 0; i < result.length; ++i) {
            final Class<?> type = this.method.getParameterTypes()[i];
            final Annotation[] annotations = this.method.getParameterAnnotations()[i];
            final Annotation bindingAnnotation = Annotations.findBindingAnnotation(
                    errors, this.method, annotations);

            final Key<?> key;
            if (bindingAnnotation == null) {
                key = Key.get(type);
            } else {
                key = Key.get(type, bindingAnnotation);
            }
            result[i] = this.injector.getInstance(key);
        }
        errors.throwProvisionExceptionIfErrorsExist();
        return result;
    }

    /**
     * Actually executes the method.
     *
     * @return The result of the method invocation.
     * @throws Throwable If method invocation failed or the method itself threw an
     *             exception.
     */
    public Object proceed() throws Throwable {
        final boolean accessible = this.method.isAccessible();
        try {
            this.method.setAccessible(true);
            return this.method.invoke(this.self, getArguments());
        } finally {
            this.method.setAccessible(accessible);
        }
    }
}
