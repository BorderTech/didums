# Didums
Dependency Injection based on JSR330.

Didums makes use of the jsr330 annotations and requires a provider like HK2 or Guice to provide the injection
functionality. A `DidumsProvider` is the interface between the Didums API and the Provider's API.

Set the `DidumsProvider` via the factory property:-
```
bordertech.factory.impl.com.github.bordertech.didums.DidumsProvider=my.didums.DidumsProviderImpl
```

The initial bindings can be setup by implementing `DidumsBinder` via the factory property:-
```
bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder=my.didums.DidumsBinder1
bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder+=my.didums.DidumsBinder2
```

Note: Multiple DidumsBinder implementations can be set.

## Factory
Provides a generic mechanism for obtaining objects which implement a requested interface. A new object will be
created each time the newInstance method is called.

The runtime `Config` class is used to look up the implementing class, based on the requested interface's
classname. This is done by prefixing the full interface name with `bordertech.factory.impl.`. For example, to specify
that the `my.example.FooImpl` implements `my.example.util.Foo` interface, the following should be added to the
configuration:
```
bordertech.factory.impl.my.example.util.Foo=my.example.FooImpl
```
Factory also supports the `Singleton` annotation to make sure only one instance of a requested class is created.

