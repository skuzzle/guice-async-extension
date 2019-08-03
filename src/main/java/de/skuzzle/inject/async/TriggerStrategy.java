package de.skuzzle.inject.async;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ServiceLoader;
import java.util.concurrent.ScheduledExecutorService;

import de.skuzzle.inject.async.annotation.Trigger;
import de.skuzzle.inject.async.internal.runnables.LockableRunnable;
import de.skuzzle.inject.async.util.InjectedMethodInvocation;

/**
 * Defines how a certain {@link Trigger} annotation is handled in order to extract
 * scheduling meta information and to actually schedule method invocations.
 * <p>
 * The framework searches for implementations of this class using the Java's
 * {@link ServiceLoader}. In order to implement your own trigger, just follow these steps:
 * </p>
 * <ol>
 * <li>Create a new Trigger annotation which will hold scheduling meta information:
 *
 * <pre>
 * &#64;Trigger
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * &#64;Target(ElementType.METHOD)
 * public &#64;interface CustomTrigger {
 *     // meta information fields
 * }
 * </pre>
 *
 * </li>
 * <li>Create an implementation of this interface to handle the new annotation:
 *
 * <pre>
 * public class CustomTriggerStrategy implements TriggerStrategy {
 *
 *     // Field injection is allowed
 *     &#64;Inject
 *     private Injector injector;
 *
 *     // public no-argument constructor required (because of the ServiceLoader)!
 *
 *     &#64;Override
 *     public Class&lt;CustomTrigger&gt; getTriggerType() {
 *         return CustomTrigger.class;
 *     }
 *
 *     &#64;Override
 *     public void schedule(Method method, Object self,
 *             ScheduledExecutorService executor) {
 *
 *         // read trigger annotation from the method
 *         CustomTrigger trigger = method.getAnnotation(getTriggerType());
 *
 *         // use meta information from the trigger to schedule the execution with the
 *         // provided executor (might want to use InjectedMethodInvocation class)
 *         executor.schedule(...);
 *     }
 * }
 * </pre>
 *
 * </li>
 * <li>Register your implementation for the ServiceLoader. Create the folder structure
 * 'META-INF/services'. Within the services folder create a file named
 * 'de.skuzzle.inject.async.TriggerStrategy'. Put a single line which contains the full
 * qualified class name of your CustomTriggerStrategy into that file.</li>
 * </ol>
 *
 * @author Simon Taddiken
 */
public interface TriggerStrategy {

    /**
     * Returns the annotation type that this strategy can handle.
     *
     * @return The annotation type.
     */
    Class<? extends Annotation> getTriggerType();

    /**
     * Extracts scheduling information from the provided {@link Method} and then schedules
     * invocations of that method according to the information.
     *
     * <p>
     * To support invocation of parameterized methods, implementors can refer to
     * {@link InjectedMethodInvocation} to inject actual parameters of a method.
     * </p>
     *
     * @param context The schedule context for the annotated method.
     * @param executor The executor to use for scheduling.
     * @param handler The exception handler to be used.
     * @param runnable A runnable that, when scheduled, will execute the annotated method.
     */
    void schedule(ScheduledContext context,
            ScheduledExecutorService executor,
            ExceptionHandler handler, LockableRunnable runnable);
}
