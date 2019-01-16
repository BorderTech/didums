package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for contract implementations.
 * <p>
 * Provides a generic mechanism for obtaining objects which implement a requested interface. A new object will be
 * created each time the newInstance method is called.</p>
 *
 * <p>
 * The runtime {@link Config} class is used to look up the implementing class, based on the requested interface's
 * classname. This is done by prefixing the full interface name with "bordertech.factory.impl.". For example, to specify
 * that the my.example.FooImpl implements my.example.util.Foo interface, the following should be added to the
 * configuration:
 * </p>
 * <pre>
 * bordertech.factory.impl.my.example.util.Foo=my.example.FooImpl
 * </pre>
 * <p>
 * Factory also supports the Singleton annotation to make sure only one instance of a requested class is created.
 * </p>
 *
 * @see Config
 * @see Didums
 *
 * @author Jonathan Austin
 * @since 1.0.0
 *
 */
public final class Factory {

	private static final Log LOG = LogFactory.getLog(Factory.class);

	private static final String PREFIX = "bordertech.factory.impl.";

	private static final Map<String, Object> SINGLETONS = new HashMap<>();

	/**
	 * Private constructor.
	 */
	private Factory() {
	}

	/**
	 * Create an instance of the implementation defined for the contract.
	 *
	 * @param <T> the contract type
	 * @param contract the contract to find and create new implementation
	 * @param qualifiers the contract qualifiers
	 * @return a new implementation of the contract, or null if no implementation defined
	 */
	public static <T> T newInstance(final Class<T> contract, final String... qualifiers) {
		String suffix = getContractSuffixKey(contract, qualifiers);
		return newInstance(suffix, null);
	}

	/**
	 * Create an instance of the implementation defined for the contract, or the default implementation if no
	 * implementation defined.
	 *
	 * @param <T> the contract type
	 * @param <U> the default contract type
	 * @param contract the contract to find and create new implementation
	 * @param defaultImpl the default implementation if an implementation is not found
	 * @param qualifiers the contract qualifiers
	 * @return a new implementation of the contract or the default implementation
	 */
	public static <T, U extends T> T newInstance(final Class<T> contract, final Class<U> defaultImpl, final String... qualifiers) {
		String suffix = getContractSuffixKey(contract, qualifiers);
		return newInstance(suffix, defaultImpl);
	}

	/**
	 * Create an instance of the implementation for the parameter key suffix.
	 *
	 * @param <T> the contract type
	 * @param keySuffix the parameter key suffix for the implementation class name
	 * @return a new implementation of the key suffix or null if no implementation defined
	 */
	public static <T> T newInstance(final String keySuffix) {
		return newInstance(keySuffix, null);
	}

	/**
	 * Create an instance of the implementation for the parameter key suffix, or the default implementation if no
	 * implementation defined.
	 *
	 * @param <T> the contract type
	 * @param keySuffix the parameter key suffix for the implementation class name
	 * @param defaultImpl the default implementation if an implementation is not found
	 * @return a new implementation of the contract or the default implementation
	 */
	public static <T> T newInstance(final String keySuffix, final Class<T> defaultImpl) {
		String implClassName = getImplClassName(keySuffix);
		Class<T> clazz;
		if (implClassName == null) {
			if (defaultImpl == null) {
				String paramKey = getParamKey(keySuffix);
				LOG.fatal("There needs to be a parameter defined for " + paramKey);
				throw new FactoryException("There needs to be a parameter defined for " + paramKey);
			}
			clazz = defaultImpl;
		} else {
			clazz = findClass(implClassName);
		}
		return createInstance(clazz);
	}

	/**
	 * Create instances of all defined implementations of the contract.
	 *
	 * @param <T> the contract type
	 * @param contract the contract to find and create new implementations
	 * @param qualifiers the contract qualifiers
	 * @return a list of implementations of the contract
	 */
	public static <T> List<T> newMultiInstances(final Class<T> contract, final String... qualifiers) {
		String suffix = getContractSuffixKey(contract, qualifiers);
		String[] classNames = getMultiImplClassName(suffix);
		List<T> impls = new ArrayList<>();
		for (String className : classNames) {
			Class<T> clazz = findClass(className);
			impls.add(createInstance(clazz));
		}
		return impls;
	}

	/**
	 * Check if the contract has an implementation (using the contracts class name as the key suffix).
	 *
	 * @param contract the contract to test if an implementation has been defined
	 * @param qualifiers the contract qualifiers
	 * @return true if an implementation is available
	 */
	public static boolean hasImplementation(final Class<?> contract, final String... qualifiers) {
		String suffix = getContractSuffixKey(contract, qualifiers);
		return hasImplementation(suffix);
	}

	/**
	 * Check if an implementation exists for the parameter key suffix.
	 *
	 * @param keySuffix the parameter key suffix to test if an implementation has been defined
	 * @return true if an implementation is available
	 */
	public static boolean hasImplementation(final String keySuffix) {
		return getImplClassName(keySuffix) != null;
	}

	/**
	 * Find the implementing class for the class name.
	 *
	 * @param <T> the contract type
	 * @param className the class name to find the class for
	 * @return the class for the name
	 */
	private static <T> Class<T> findClass(final String className) {
		try {
			return (Class<T>) Class.forName(className.trim());
		} catch (ClassNotFoundException e) {
			throw new FactoryException("Class [" + className + "] not found.", e);
		}
	}

	/**
	 * Create an instance of the class that also honors the Singleton annotation.
	 *
	 * @param <T> the contract type
	 * @param clazz the class to create an instance
	 * @return a new class instance
	 */
	private static <T> T createInstance(final Class<T> clazz) {

		// Check singleton annotation
		if (clazz.getAnnotation(Singleton.class) != null) {
			return createSingletonInstance(clazz);
		}

		try {
			return clazz.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new FactoryException("Failed to instantiate object of class " + clazz.getName(), e);
		}
	}

	/**
	 * Create a singleton instance of the class.
	 *
	 * @param <T> the contract type
	 * @param clazz the class to create an instance
	 * @return a new class instance
	 */
	private static synchronized <T> T createSingletonInstance(final Class<T> clazz) {

		// Check already have an instance
		String key = clazz.getName();
		if (SINGLETONS.containsKey(key)) {
			return (T) SINGLETONS.get(clazz.getName());
		}

		// Create a single instance and put in the MAP
		try {
			T obj = clazz.newInstance();
			SINGLETONS.put(key, obj);
			return obj;
		} catch (IllegalAccessException | InstantiationException e) {
			throw new FactoryException("Failed to instantiate object of class " + clazz.getName(), e);
		}
	}

	/**
	 * Retrieve the implementation class name for the parameter key suffix.
	 *
	 * @param suffixKey the parameter key suffix
	 * @return the implementing class name, or null if no implementation
	 */
	private static String getImplClassName(final String suffixKey) {
		String paramKey = getParamKey(suffixKey);
		return Config.getInstance().getString(paramKey);
	}

	/**
	 * Retrieve multiple implementation class names for the parameter key suffix.
	 *
	 * @param suffixKey the parameter key suffix
	 * @return the implementing class names, or an empty array
	 */
	private static String[] getMultiImplClassName(final String suffixKey) {
		String paramKey = getParamKey(suffixKey);
		return Config.getInstance().getStringArray(paramKey);
	}

	/**
	 * Append the suffix key to the standard factory prefix.
	 *
	 * @param suffixKey the parameter key suffix
	 * @return the fully qualified parameter key
	 */
	private static String getParamKey(final String suffixKey) {
		return PREFIX + suffixKey;
	}

	/**
	 * Build the contract suffix with qualifiers.
	 *
	 * @param contract the contract type
	 * @param qualifiers the contract qualifiers
	 * @return the contract parameter suffix
	 */
	private static String getContractSuffixKey(final Class contract, final String... qualifiers) {
		StringBuilder suffix = new StringBuilder(contract.getName());
		for (String qualifier : qualifiers) {
			if (!StringUtils.isEmpty(qualifier)) {
				suffix.append(".").append(qualifier);
			}
		}
		return suffix.toString();
	}

}
