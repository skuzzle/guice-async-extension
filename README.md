[![Build Status](https://travis-ci.org/skuzzle/guice-scoped-proxy-extension.svg?branch=master)](https://travis-ci.org/skuzzle/guice-scoped-proxy-extension) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-scoped-proxy-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.inject/guice-scoped-proxy-extension)
[![Coverage Status](https://coveralls.io/repos/skuzzle/guice-scoped-proxy-extension/badge.svg?branch=master&service=github)](https://coveralls.io/github/skuzzle/guice-scoped-proxy-extension?branch=master)
# Guice Scoped Proxies

Bind classes as scoped proxies to inject them into wider scopes without the need to use
a `Provider`.

Sample usage:
```java
public class MyModule extends AbstractModule {

    @Override
    public void configure() {
        ScopedProxyBinder.using(binder())
                .bind(MyInterface.class)
                .to(MyInterfaceImpl.class)
                .in(SessionScoped.class);
    }
}
```

Now you can inject `MyInterface` into every scope as if it were a Singleton or as if it 
were a `Provider<MyInterface>`.

## Known Issues
* Currently it is not possible to add untargetted bindings. You always have to name the 
  implementing class by using either of the provided `to(...)` methods.