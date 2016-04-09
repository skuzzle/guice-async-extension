package de.skuzzle.inject.async.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;

import de.skuzzle.inject.async.Futures;
import de.skuzzle.inject.async.GuiceAsync;

/**
 * Tags a method to be run asynchronously. Annotated methods must return either
 * a {@link Future} or no value (void or {@link Void}). To enable asynchronous
 * method support, use {@link GuiceAsync#enableFor(com.google.inject.Binder)} in
 * any of your modules.
 * <p>
 * Method calls are intercepted and the call is then executed in a different
 * thread using an {@link ExecutorService}. The ExecutorService instance that
 * will be used is looked up using the {@link Injector}.
 * </p>
 *
 * <h2>Specifying the Executor</h2>
 * It is possible to customize the look-up {@link Key} that is used to retrieve the actual
 * ExecutorService.
 * <ul>
 * <li>You can put any {@link BindingAnnotation} including {@link Named} on the
 * method to specify the annotation part of the Key.</li>
 * <li>If you need to reference a certain ExecutorService sub type, you can use
 * {@link Executor} to specify the class part of the Key.</li>
 * </ul>
 *
 * For example, if your method may look like:
 *
 * <pre>
 * &#64;Async
 * &#64;Named("computationExecutor")
 * &#64;Executor(AdvancedExecutorService.class)
 * public void compute() {...}
 * </pre>
 * <p>
 * In this case, the Executor to use will be looked up like
 * {@code injector.getInstance(Key.get(AdvancedExecutorService.class,
 *      Names.named("computationExecutor"))}
 * </p>
 * <p>
 * If you leave out the {@link Executor} part, the class defaults to
 * {@code ExecutorService.class}. If you leave out the binding annotation, the
 * created key will not have an annotation part. If you put neither a binding annotation
 * nor an Executor class on the method, a default ExecutorService is used that is created
 * internally. You should make no assumptions about the actual behavior of that service
 * and it is highly recommended to bind and specify an ExecutorService yourself.
 * </p>
 *
 * <pre>
 * public class MyModule extends AbstractModule {
 *     &#64;Provides
 *     public ThreadFactory provideMyThreadFactory() {
 *         return new ThreadFactoryBuilder()....build(); // class from Google guava
 *     }
 *
 *     &#64;Provides
 *     &#64;Named("mainThreadPool")
 *     public ExecutorService provideMyExecutor(ThreadFactory threadFactory) {
 *         return Executors.newFixedThreadPool(4, threadFactory);
 *     }
 * }
 * </pre>
 *
 * <h2>Returning values</h2> You can return a value from an asynchronous method
 * by returning a {@link Future} object. To implement such a method, you can
 * obtain a Future for an arbitrary value using {@link Futures#delegate(Object)}
 * like in:
 *
 * <pre>
 * &#64;Async
 * public Future&lt;Double&gt; compute() {
 *     final double result = realComputation();
 *     return Futures.delegate(result);
 * }
 * </pre>
 *
 * The dummy Future object created here will be replaced with a real Future
 * object that is obtained from the ExecutorService.
 *
 * <h2>Thread safety</h2> As your method is executed asynchronously, special
 * care has to be taken to ensure thread safety of that operation. Beware of the
 * following:
 * <ul>
 * <li>Do not access attributes of the surrounding class. If you do, use
 * synchronization to ensure visibility of the data that you write and read .</li>
 * <li>Parameters that are passed to the asynchronous method are also
 * transferred to the executing thread. Thus, all actual parameters must be
 * thread safe. That is, they must use synchronization and/or volatile
 * declarations to ensure integrity and visibility of the data that is written
 * and read.</li>
 * </ul>
 *
 * <h2>Exception handling</h2> Asynchronous methods can throw checked
 * exceptions. If they do, those exceptions will be delegated to the
 * {@link UncaughtExceptionHandler} of the executing thread. In most
 * implementations such a handler can be supplied to an ExecutorService by
 * passing a {@link ThreadFactory} upon construction.
 *
 * <p>
 * This annotation is subject to all limitations that apply to
 * {@link MethodInterceptor MethodInterceptors}. Please see the guice wiki for
 * more information.
 * </p>
 *
 * @author Simon Taddiken
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface Async {

}
