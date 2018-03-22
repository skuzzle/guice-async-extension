[![Build Status](https://travis-ci.org/skuzzle/guice-async-extension.svg?branch=master)](https://travis-ci.org/skuzzle/guice-async-extension) 
[![Coverage Status](https://coveralls.io/repos/skuzzle/guice-async-extension/badge.svg?branch=master&service=github)](https://coveralls.io/github/skuzzle/guice-async-extension?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-async-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-async-extension)
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
    
    @Async
    public CompletableFuture<Integer> asynchronousWithCompletableResult() {
        return Futures.delegateCompletable(1337);
    }
}
```

Please have a look at the [wiki](https://github.com/skuzzle/guice-async-extension/wiki)
for detailed setup and usage instructions (spoiler: it's easy!)