[![Build Status](https://travis-ci.org/skuzzle/guice-async-extension.svg?branch=master)](https://travis-ci.org/skuzzle/guice-async-extension) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-async-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-async-extension)
[![Coverage Status](https://coveralls.io/repos/skuzzle/guice-async-extension/badge.svg?branch=master&service=github)](https://coveralls.io/github/skuzzle/guice-async-extension?branch=master)
[![JavaDoc](http://javadoc-badge.appspot.com/de.skuzzle.inject/guice-async-extension.svg?label=JavaDoc)](http://javadoc-badge.appspot.com/de.skuzzle.inject/guice-async-extension)

# Guice Asynchronous Methods

Execute arbitrary methods asynchronously or periodically by marking them with an 
annotation. Quickstart sample:

```java
public class MyService {

    @Scheduled
    @CronTrigger("0 0 0 * * *")
    public void executePeriodic(SomeService injectedParameter) {
        // ...
    }
    
    @Async
    public void executeAsynchronously(SomeService someService) {
    }
    
    @Async
    public Future<Integer> asynchronousWithResult() {
        return Futures.delegate(1337);
    }
}
```

## Enable asynchronous support
In order to use the annotations shown in the quickstart example, you must enable 
asynchronous support for your injector within a `Module`:
```java
import de.skuzzle.inject.async.GuiceAsync;

public class MyModule extends AbstractModule {

    @Override
    public void configure() {
        GuiceAsync.enableFor(binder());
    }
}
```

## Scheduled execution
A method can be marked with `@Scheduled` to have its execution scheduled at a certain 
periodicity. The periodicity is specified by an additional _trigger annotation_. For 
example you can use a `@CronTrigger` for defining complex scheduling plans:

```java
@Scheduled
@Crontrigger("0 0 12 ? * WED") // execute every wednesday at 12pm
public void scheduleMethod() {
    // do something
}
```
A method is scheduled according to its trigger annotation by the time an object of its 
containing type is constructed by the `Injector`.

### Parameter injection
Scheduled methods can have parameters. They will be injected prior to each invocation.

```java
@Scheduled
@CronTrigger("...")
public void methodWithDependencies(@Named("test") SomeService someService) {
    //...
}
```

## Asynchronous execution
A method can be marked with `@Async` to have every call to it intercepted and executed in
a different thread:

```java
@Async
public void doParallel() {
    // do something
}
```

### Returning values
Asynchronous methods can return values by wrapping them in a `Future` object.

```java
@Async
public Future<Integer> compute(int n) {
    final int result = doActualCompute();
    return Futures.delegate(result);
}
```


## Specifying the executor or scheduler to use.
Methods are called asynchronously using an `ExecutorService` and scheduled using a 
`ScheduledExecutorService`. For each annotated method you can define a `Key` that is 
used to look up the actual implementation. In case of asynchronous methods, use the 
`@Executor` annotation for specifying the Executor type. For scheduled methods, use
`@Scheduler` instead. Optionally you can put a _binding annotation_ to further refine the 
Key.

```java
    @Async
    @Executor(MyVerySpecialCustomExecutor.class)
    @Named("firstVerySpecialCustomExecutor")
    public void sendMail(MailOptions options) {
        // Executor will be retrieved using Key.get(MyVerySpecialCustomExecutor.class, 
        //     Names.named("firstVerySpecialCustomExecutor"))
    }
    
    @Async
    @Named("sendMailThread")
    public void sendMailAlternatively(MailOptions options) {
        // Executor will be retrieved using Key.get(ExecutorService.class, 
        //     Names.named("firstVerySpecialCustomExecutor"))
    }
    
    @Scheduled
    @CronTrigger("...")
    @Scheduler(MySchedulerImplementation.class)
    public void scheduledMethod() {
    }
}
```

## Exceptions

All exceptions that are thrown by a method that is executed asynchronously are delegated
to the thread's `UncaughtExceptionHandler`.