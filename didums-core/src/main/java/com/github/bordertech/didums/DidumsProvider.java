package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import java.lang.annotation.Annotation;

/**
 * Dependency Injection Provider.
 * <p>
 * Implementors of this interface provide the bridge between the Didums API and the functionality of the backing
 * Dependency Injection framework.
 * </p>
 * <p>
 * Didums checks the following factory property to determine the provider implementation to use:-
 * </p>
 *
 * <pre>
 * bordertech.factory.impl.com.github.bordertech.didums.DidumsProvider=my.didums.DidumsProviderImpl
 * </pre>
 *
 *
 * @see Didums
 * @see Config
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface DidumsProvider {

	/**
	 * Retrieve the implementation for this service and qualifiers.
	 *
	 * @param <T> the service contract class type
	 * @param contract the service contract class
	 * @param qualifiers the service qualifiers
	 * @return the implementation for this service and qualifiers or null if none available
	 */
	<T> T getService(final Class<T> contract, final Annotation... qualifiers);

	/**
	 * Create and inject a class with its dependencies.
	 *
	 * @param <T> the class type
	 * @param createMe the class to create an instance of
	 * @return an instance of this class with injected dependencies
	 */
	<T> T createAndInject(final Class<T> createMe);

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
	<T, U extends T> void bind(final Class<T> contract, final Class<U> contractImpl, final boolean singleton, final Annotation... qualifiers);
}
