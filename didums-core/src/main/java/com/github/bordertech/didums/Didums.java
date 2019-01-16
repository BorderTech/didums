package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Dependency Injector Helper.
 * <p>
 * Didums makes use of the jsr330 annotations and requires a provider like HK2 or Guice to provide the injection
 * functionality. A {@link DidumsProvider} is the interface between the Didums API and the Provider's API.
 * </p>
 * <p>
 * Set the {@link DidumsProvider} via the factory property:-
 * </p>
 * <pre>
 * bordertech.factory.impl.com.github.bordertech.didums.DidumsProvider=my.didums.DidumsProviderImpl
 * </pre>
 * <p>
 * The initial bindings can be setup by implementing {@link DidumsBinder} via the factory property.
 * </p>
 * <pre>
 * bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder=my.didums.DidumsBinder1
 * bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder+=my.didums.DidumsBinder2
 * </pre>
 * <p>
 * Note: Multiple DidumsBinder implementations can be set.
 * </p>
 *
 * @see Config
 * @see Factory
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class Didums {

	private static final DidumsProvider PROVIDER;

	static {
		// Load the Provider via the tradional Factory
		// TODO Also use SPI
		PROVIDER = Factory.newInstance(DidumsProvider.class, DefaultDidumsProvider.class);
		// Load the Bunders (if any)
		List<DidumsBinder> binders = Factory.newMultiInstances(DidumsBinder.class);
		for (DidumsBinder binder : binders) {
			binder.configBindings(PROVIDER);
		}
	}

	/**
	 * Private constructor.
	 */
	private Didums() {
	}

	/**
	 * Check if an implementation is available for this service and qualifiers.
	 *
	 * @param <T> the service class type
	 * @param service the service class
	 * @param qualifiers the service qualifiers
	 * @return true if an implementation is available for this service and qualifiers
	 */
	public static <T> boolean hasService(final Class<T> service, final Annotation... qualifiers) {
		// Provider
		T impl = PROVIDER.getService(service, qualifiers);
		if (impl != null) {
			return true;
		}
		// Fallback to basic factory
		return Factory.hasImplementation(service, buildFactoryQualifiers(qualifiers));
	}

	/**
	 * Retrieve the implementation for this service and qualifiers. Fallsback to the Factory Implementation if there is
	 * no binding.
	 *
	 * @param <T> the service class type
	 * @param service the service class
	 * @param qualifiers the service qualifiers
	 * @return the implementation for this service and qualifiers or null if none available
	 */
	public static <T> T getService(final Class<T> service, final Annotation... qualifiers) {
		// Provider
		T impl = PROVIDER.getService(service, qualifiers);
		// Fallback to basic factory
		if (impl == null) {
			impl = Factory.newInstance(service, buildFactoryQualifiers(qualifiers));
		}
		return impl;
	}

	/**
	 * Retrieve the implementation for this service and qualifiers. Fallsback to the Factory Implementation if there is
	 * no binding.
	 *
	 * @param <T> the service class type
	 * @param service the service class
	 * @param defaultImpl the default implementation if an implementation is not found
	 * @param qualifiers the service qualifiers
	 * @return the implementation for this service and qualifiers or null if none available
	 */
	public static <T> T getService(final Class<T> service, final Class<T> defaultImpl, final Annotation... qualifiers) {
		// Provider
		T impl = PROVIDER.getService(service, qualifiers);
		// Fallback to basic factory
		if (impl == null) {
			impl = Factory.newInstance(service, buildFactoryQualifiers(qualifiers));
		}
		return impl;
	}

	/**
	 * Create and inject a class with its dependencies.
	 *
	 * @param <T> the class type
	 * @param createMe the class to create an instance of
	 * @return an instance of this class with injected dependencies
	 */
	public static <T> T createAndInject(final Class<T> createMe) {
		return PROVIDER.createAndInject(createMe);
	}

	/**
	 * Bind a Singleton implementation to a service contract and qualifiers.
	 *
	 * @param <T> the service class type
	 * @param <U> the service implementation type
	 * @param contract the service contract to bind the implementation to
	 * @param contractImpl the service contract implementation class to bind
	 * @param qualifiers the service qualifiers
	 */
	public static <T, U extends T> void bind(final Class<T> contract, final Class<U> contractImpl, final Annotation... qualifiers) {
		PROVIDER.bind(contract, contractImpl, true, qualifiers);
	}

	/**
	 * Bind an implementation to a service contract and qualifiers.
	 *
	 * @param <T> the service class type
	 * @param <U> the service implementation type
	 * @param contract the service contract to bind the implementation to
	 * @param contractImpl the service contract implementation class to bind
	 * @param singleton true if bind as a Singleton
	 * @param qualifiers the service qualifiers
	 */
	public static <T, U extends T> void bind(final Class<T> contract, final Class<U> contractImpl,
			final boolean singleton, final Annotation... qualifiers) {
		PROVIDER.bind(contract, contractImpl, singleton, qualifiers);
	}

	/**
	 * Create factory parameter qualifiers.
	 * <p>
	 * This does depend on the toString on the annotation to be appropriate for parameter keys.
	 * </p>
	 *
	 * @param qualifiers the service qualifiers
	 * @return the array of parameter keys
	 */
	private static String[] buildFactoryQualifiers(final Annotation[] qualifiers) {
		if (qualifiers == null || qualifiers.length == 0) {
			return new String[0];
		}
		List<String> keys = new ArrayList<>();
		for (Annotation qualifier : qualifiers) {
			keys.add(qualifier.toString());
		}
		return keys.toArray(new String[0]);
	}

}
