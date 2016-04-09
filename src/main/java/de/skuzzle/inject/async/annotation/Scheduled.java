package de.skuzzle.inject.async.annotation;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;

import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;

import de.skuzzle.inject.async.TriggerStrategy;

/**
 * Marks a method to be executed periodically. In order for a method to be
 * eligible for periodic execution, it must additionally be annotated with
 * exactly one {@link Trigger trigger annotation}. The trigger defines the way
 * in which the method will be scheduled. Possible triggers are:
 * <ul>
 * <li>{@link SimpleTrigger} - Define a delay or rate at which the method should
 * be scheduled.</li>
 * <li>{@link CronTrigger} - Define a complex cron job pattern to define the
 * periodicity.</li>
 * <li>Custom - You can provide a custom {@link TriggerStrategy} using Java's
 * <em>Service Provider Interfaces</em>.</li>
 * </ul>
 *
 * Methods are scheduled for execution by the time an instance of their
 * containing type is constructed using the {@link Injector}.
 *
 * <h2>Specifying the Scheduler</h2>
 * <p>
 * Methods are scheduled by using an implementation of
 * {@link ScheduledExecutorService}. You can provide a {@link Key} that will be
 * used to look up the scheduler implementation on a per-method basis.
 * </p>
 * <ul>
 * <li>You can put any {@link BindingAnnotation} including {@link Named} on the
 * method to specify the annotation part of the Key.</li>
 * <li>If you need to reference a certain ScheduledExecutorService sub type, you
 * can use {@link Scheduler} to specify the class part of the Key.</li>
 * </ul>
 *
 * For example, if your method may look like:
 *
 * <pre>
 * &#64;Scheduled
 * &#64;SimpleTrigger(5000) // execute every 5 seconds
 * &#64Named("mainScheduler")
 * &#64;Scheduler(AdvancedScheduler.class)
 * public void compute() {...}
 * </pre>
 * <p>
 * In this case, the Scheduler to use will be looked up like
 * {@code injector.getInstance(Key.get(AdvancedScheduler.class,
 *      Names.named("mainScheduler"))}
 * </p>
 * <p>
 * If you leave out the {@link Scheduler} part, the class defaults to
 * {@code ScheduledExecutorService.class}. If you leave out the binding
 * annotation, the created key will not have an annotation part. If you put
 * neither a binding annotation nor a Scheduler class on the method, a default
 * ScheduledExecutorService is used which is created internally. You should make
 * no assumptions about the actual behavior of that service and it is highly
 * recommended to bind and specify an ScheduledExecutorService yourself.
 * </p>
 *
 * <pre>
 * public class MyModule extends AbstractModule {
 *     &#64Provides
 *     public ThreadFactory provideMyThreadFactory() {
 *         return new ThreadFactoryBuilder()....build(); // class from Google guava
 *     }
 *
 *     &#64;Provides
 *     &#64;Named("mainScheduler")
 *     public ScheduledExecutorService provideMyExecutor(ThreadFactory threadFactory) {
 *         return Executors.newScheduledThreadPool(NUM_OF_THREADS, threadFactory);
 *     }
 * }
 * </pre>
 *
 * <h2>Injecting parameters</h2> The framework will take care of injecting all
 * parameters of a scheduled method when it is being executed. The injected
 * objects are retrieved from the {@link Injector} right before every execution
 * of the method.
 * <pre>
 * &#64;Scheduled
 * &#64;SimpleTrigger(5000) // execute every 5 seconds
 * public void compute(ComputationService service,
 *     &#64;Named("threshold") int computationThreshold) {...}
 * </pre>
 *
 * <h2>Thread safety</h2> As your method is executed asynchronously, special
 * care has to be taken to ensure thread safety of that operation. Beware of the
 * following:
 * <ul>
 * <li>Do not access attributes of the surrounding class. If you do, use
 * synchronization to ensure visibility of the data that you write and read .
 * </li>
 * <li>Parameters that are passed to the asynchronous method are also
 * transferred to the executing thread. Thus, all actual parameters must be
 * thread safe. That is, they must use synchronization and/or volatile
 * declarations to ensure integrity and visibility of the data that is written
 * and read.</li>
 * </ul>
 *
 * <h2>Exception handling</h2> Scheduled methods can throw checked exceptions.
 * If they do, those exceptions will be delegated to the
 * {@link UncaughtExceptionHandler} of the executing thread. In most
 * implementations such a handler can be supplied to an ExecutorService by
 * passing a {@link ThreadFactory} upon construction.
 *
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Scheduled {

}
