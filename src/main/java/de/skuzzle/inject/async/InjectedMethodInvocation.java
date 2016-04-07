package de.skuzzle.inject.async;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;

/**
 * Allows to invoke any method without explicitly providing its arguments.
 * Arguments are looked up by creating {@link Key} objects from the method's
 * parameter types and then passing them to an {@link Injector} to retrieve the
 * actual argument.
 * <p>
 * A {@link BindingAnnotation} may optionally be present on an argument type to
 * refine the Key that is created to look up the parameter's actual value.
 * </p>
 *
 * @author Simon Taddiken
 */
public class InjectedMethodInvocation implements MethodInvocation {

    private final Injector injector;
    private final Object self;
    private final Method method;

    private InjectedMethodInvocation(Injector injector, Object self, Method method) {
        this.injector = injector;
        this.self = self;
        this.method = method;
    }

    /**
     * Creates a {@link MethodInvocation} object that is able to invoke the
     * provided static method. The actual parameters for the invocation will be
     * looked up using the given injector.
     * <p>
     * You can call {@link MethodInvocation#proceed()} to invoke the method.
     * </p>
     *
     * @param method A static method.
     * @param injector The injector which is queried for actual method
     *            arguments.
     * @return The MethodInvocation.
     */
    public static MethodInvocation forStatic(Method method, Injector injector) {
        checkArgument(method != null);
        checkArgument(Modifier.isStatic(method.getModifiers()));
        checkArgument(injector != null);
        return new InjectedMethodInvocation(injector, null, method);
    }

    /**
     * Creates a {@link MethodInvocation} object that is able to invoke the
     * provided non-static method. The actual parameters for the invocation will
     * be looked up using the given injector.
     * <p>
     * You can call {@link MethodInvocation#proceed()} to invoke the method.
     * </p>
     *
     * @param method A non-static method.
     * @param self The object on which the method will be called.
     * @param injector The injector which is queried for actual method
     *            arguments.
     * @return The MethodInvocation.
     */
    public static MethodInvocation forMethod(Method method, Object self,
            Injector injector) {
        checkArgument(method != null);
        checkArgument(injector != null);
        return new InjectedMethodInvocation(injector, self, method);
    }

    @Override
    public Object[] getArguments() {
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

    @Override
    public Object proceed() throws Throwable {
        return this.method.invoke(this.self, getArguments());
    }

    @Override
    public Object getThis() {
        return this.self;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return this.method;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
}
