# Didums

## Status

[![Build Status](https://travis-ci.com/BorderTech/didums.svg?branch=master)](https://travis-ci.com/BorderTech/didums)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bordertech-didums&metric=alert_status)](https://sonarcloud.io/dashboard?id=bordertech-didums)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=bordertech-didums&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=bordertech-didums)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=bordertech-didums&metric=coverage)](https://sonarcloud.io/dashboard?id=bordertech-didums)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/60fbedbceee84805a244d89182a41310)](https://www.codacy.com/app/BorderTech/didums?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=BorderTech/didums&amp;utm_campaign=Badge_Grade)
[![Javadocs](https://www.javadoc.io/badge/com.github.bordertech.didums/didums-core.svg)](https://www.javadoc.io/doc/com.github.bordertech.didums/didums-core)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.bordertech.didums/didums-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.bordertech.didums%22%20AND%20a:%22didums-core%22)

## Content

- [What is Didums](#what-is-didums)
- [Why use Didums](#why-use-didums)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Links](#links)
- [Contributing](#contributing)

## What is Didums

Didums is a facade for Dependency Injection frameworks that implement [JSR330](http://javax-inject.github.io/javax-inject/).

## Why use Didums

Dependency Injection frameworks (e.g. Guice, Weld, HK2) have existed for years with a variety of APIs for configuring and injecting.

Didums serves as a simple facade or abstraction for these DI frameworks allowing the end user to plug in the desired DI framework at deployment time.

Even if full blown Dependency Injection is not needed, Didums provides a flexible Factory pattern for binding implementations to interfaces.

## Getting started

Add `didums-core` dependency:

``` xml
<project>
  ....
  <dependency>
    <groupId>com.github.bordertech.didums</groupId>
    <artifactId>didums-core</artifactId>
    <version>1.0.4</version>
  </dependency>
  ....
</project>
```

Create an interface `Foo`:

``` java
package my.example;
public interface Foo {
}
```

Create an implementation `FooImpl`:

``` java
package my.example;
public class FooImpl implements Foo {
}
```

Use Didums to create the instance of `Foo`:

``` java
  Foo foo = Didums.getService(Foo.class);
```

The runtime implementation can be set or overridden via [Factory Binding](#factory-binding) or [Didums Binding](#didums-binding).

As the default implementation is already available, it can be provided on the `getService` method:

```
  Foo foo = Didums.getService(Foo.class, FooImpl.class);
```

### Factory Binding

Didums can be used without a DI backing framework as it provides a fallback Factory pattern. The only JSR330 annotation supported is `Singleton`.

Didums will check for a runtime property to get the required implementation class name.

The property used is the prefix `bordertech.factory.impl` combined with the interface class name:

```
bordertech.factory.impl.my.example.Foo=my.example.FooImpl
```

Qualifiers can also be used to specify the implementation class:

``` java
  Foo foo = Didums.getService(Foo.class, "use", "another");
```

The qualifiers are concatenated to the parameter key lookup with a "." separator between the qualifiers:

```
bordertech.factory.impl.my.example.util.Foo.use.another=my.example.FooAnotherImpl
```

Refer to [Config](https://github.com/BorderTech/java-config) on how to set runtime properties.

### Didums Binding

Using a backing DI Framework via a provider allows the full range of JSR330 annotations to be used.

Add a `DI Provider` dependency:

``` xml
<project>
  ....
  <dependency>
    <groupId>com.github.bordertech.didums</groupId>
    <artifactId>didums-hk2</artifactId>
    <version>1.0.4</version>
  </dependency>
  ....
</project>
```

When using a DI provider, a `DidumsBinder` implementation can be used to bind interfaces and implementations:

``` java
package my.example;
public class MyDidumsBinder implements DidumsBinder {

  @Override
  public void configBindings(final DidumsProvider provider) {
    provider.bind(Foo.class, FooImpl.class, false);
    provider.bind(AnotherFoo.class, AnotherFooImpl.class, false);
  }

}
```

Set the `DidumsBinder` factory property:

```
bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder=my.example.MyDidumsBinder
bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder+=my.example.AnotherDidumsBinder
```

As shown in the example above, multiple `DidumsBinder` implementations can be set.

Projects can also use the Binding method supported by its selected DI framework.

If an interface has not been bound via a `DidumsBinder`, then Didums will fallback to the Factory pattern binding.

Refer to [Config](https://github.com/BorderTech/java-config) on how to set runtime properties.

## Configuration

### DidumsProvider

Implementations of the `DidumsProvider` interface are the bridge between the Didums API and the backing DI framework.

If creating a custom provider, set the `DidumsProvider` implementation via the factory property:

```
bordertech.factory.impl.com.github.bordertech.didums.DidumsProvider=my.didums.DidumsProviderImpl
```

Or add a predefined `DI Provider` dependency:

``` xml
<project>
  ....
  <dependency>
    <groupId>com.github.bordertech.didums</groupId>
    <artifactId>didums-hk2</artifactId>
    <version>1.0.4</version>
  </dependency>
  ....
</project>
```

## Links

### DI Frameworks

- [Weld](http://weld.cdi-spec.org)
- [OpenWebBeans](http://openwebbeans.apache.org)
- [Guice](https://github.com/google/guice)
- [HK2](https://javaee.github.io/hk2)
- [Commons-inject](https://commons.apache.org/sandbox/commons-inject)
- [Dagger](https://github.com/google/dagger)
- [Spring](https://docs.spring.io/spring/docs/4.3.12.RELEASE/spring-framework-reference/htmlsingle/#overview-dependency-injection)
- [Picocontainer](http://picocontainer.com/)

### Interesting links

- https://github.com/javax-inject/javax-inject
- https://stackoverflow.com/questions/2652126/google-guice-vs-jsr-299-cdi-weld
- https://ceylon-lang.org/blog/2015/12/01/weld-guice/
- https://medium.com/@varago.rafael/introduction-to-google-guice-for-di-43b63a48f231
- https://dzone.com/articles/an-opinionless-comparison-of-spring-and-guice
- https://www.mkyong.com/spring3/spring-3-and-jsr-330-inject-and-named-example/
- https://www.martinfowler.com/articles/injection.html
- https://medium.com/@varago.rafael/managing-coupling-with-dependency-injection-46157eb1dc4d

### DI and Related JSRs

Dependency Injection is defined via [JSR330](http://javax-inject.github.io/javax-inject/). However there are other JSRs that expand JSR330 and can cause some confusion.

How are JSR330, JSR299, JSR250, JSR365, JSR346 related:

- http://www.adam-bien.com/roller/abien/entry/what_is_the_relation_between
- https://dzone.com/articles/what-relation-betwe-there
- https://dzone.com/articles/jsr-365-update-digging-into-cdi-20
- https://www.javacodegeeks.com/2017/03/jsr-365-update-digging-cdi-2-0.html
- https://en.wikipedia.org/wiki/JSR_250

### Using Didums and Jersey

If your project uses [Jersey](https://jersey.github.io/), then using Didums makes it even easier to define your bindings as Jersey uses [HK2](https://javaee.github.io/hk2).

- http://appsdeveloperblog.com/dependency-injection-hk2-jersey-jax-rs/
- https://riptutorial.com/jersey/example/23632/basic-dependency-injection-using-jersey-s-hk2

## Contributing

Refer to these guidelines for [Workflow](https://github.com/BorderTech/java-common/wiki/Workflow) and [Releasing](https://github.com/BorderTech/java-common/wiki/Releasing).
