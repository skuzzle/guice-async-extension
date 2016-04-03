[![Build Status](https://travis-ci.org/skuzzle/guice-async-extension.svg?branch=master)](https://travis-ci.org/skuzzle/guice-async-extension) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-async-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-async-extension)
[![Coverage Status](https://coveralls.io/repos/skuzzle/guice-async-extension/badge.svg?branch=master&service=github)](https://coveralls.io/github/skuzzle/guice-async-extension?branch=master)
# Guice Asynchronous Methods

Execute arbitrary methods asynchronously by simply putting an `@Async` annotation on them.

### Sample usage:

First, enable Asynchronous Method support in any of your modules:

```java
import de.skuzzle.inject.async.GuiceAsync;

public class MyModule extends AbstractModule {

    @Override
    public void configure() {
        GuiceAsync.enableFor(binder());
    }

}
```

Now, mark a method within any injected class to be executed asynchronously with the 
default `ExecutorService`:

```java
import de.skuzzle.inject.async.Async;

public class MailService {

    @Async
    public void sendMail(MailOptions options) {
        // all the logic
    }
}
```

Now inject your service and call the method:

```java
public class MailController {

    @Inject
    private MailService mailService;
    
    public void sendMail() {
        final MailOptions options = prepareOptions();
        mailService.sendMail(options); // returns immediately
    }
}
```

### Returning values

You can also return values that have been calculated asynchronously by giving your method
a `Future` return type:

```java
import de.skuzzle.inject.async.Async;
import de.skuzzle.inject.async.Futures;

public class MailService {

    @Async
    public Future<MailResult> sendMail(MailOptions options) {
        final MailResult result = actuallySendTheMail(options);
        return Futures.delegate(result);
    }
}
```

### Defining an `ExecutorService` to use

By default, all `@Async` methods are executed by a `CachedExecutorService`. It is easy 
to customize by providing the `Key` that the `Injector` will use to look up the 
`ExecutorService` instance:

```java
public class MyModule extends AbstractModule {

    @Provides
    @Named("sendMailThread")
    public ExecutorService provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Named("firstVerySpecialCustomExecutor")
    public MyVerySpecialCustomExecutor provideCustomExecutor() {
        return new MyVerySpecialCustomExecutor();
    }
}
```

Use a custom `ExecutorService`:

```java
import de.skuzzle.inject.async.Executor;

public class MailService {

    @Async
    @Executor(MyVerySpecialCustomExecutor.class)
    @Named("firstVerySpecialCustomExecutor")
    public void sendMail(MailOptions options) {
        // all the logic
    }
    
    @Async
    @Named("sendMailThread")
    public void sendMailAlternatively(MailOptions options) {
        // all the logic
    }
}
```

### Exceptions

All exceptions that are thrown by a method that is executed asynchronously are delegated
to the thread's `UncaughtExceptionHandler`.