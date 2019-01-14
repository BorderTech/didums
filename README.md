# Didums
Dependency Injection based on JSR330.

## Status
[![Build Status](https://travis-ci.com/BorderTech/didums.svg?branch=master)](https://travis-ci.com/BorderTech/didums)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/60fbedbceee84805a244d89182a41310)](https://www.codacy.com/app/BorderTech/didums?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=BorderTech/didums&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/60fbedbceee84805a244d89182a41310)](https://www.codacy.com/app/BorderTech/didums?utm_source=github.com&utm_medium=referral&utm_content=BorderTech/didums&utm_campaign=Badge_Coverage)
[![Javadocs](https://www.javadoc.io/badge/com.github.bordertech.didums/didums-core.svg)](https://www.javadoc.io/doc/com.github.bordertech.didums/didums-core)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.bordertech.didums/didums-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.bordertech.didums%22%20AND%20a:%22didums-core%22)

## Why Use Didums?
TODO

## How Didums Works

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

