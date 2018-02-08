package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
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
 * @author Jonathan Austin
 * @since 1.0.0
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
	 * @param <T> the contract type
	 * @param contract the contract to find and create new implementation
	 * @return a new implementation of the contract
	 */
	public static <T> T newInstance(final Class<T> contract) {
		return newInstance(contract, null);
	}

	/**
	 * @param <T> the contract type
	 * @param <U> the default contract type
	 * @param contract the contract to find and create new implementation
	 * @param defaultImpl the default implementation if an implementation is not found
	 * @return a new implementation of the contract or the default implementation
	 */
	public static <T, U extends T> T newInstance(final Class<T> contract, final Class<U> defaultImpl) {
		return newInstance(contract.getName(), defaultImpl);
	}

	/**
	 * @param <T> the contract type
	 * @param keySuffix the parameter key suffix for the implementation class name
	 * @return a new implementation of the contract
	 */
	public static <T> T newInstance(final String keySuffix) {
		return newInstance(keySuffix, null);

	}

	/**
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
	 * @param <T> the contract type
	 * @param contract the contract to find and create new implementations
	 * @return a list of implementations of the contract
	 */
	public static <T> List<T> newMultiInstances(final Class<T> contract) {
		String[] classNames = getMultiImplClassName(contract.getName());
		List<T> impls = new ArrayList<>();
		for (String className : classNames) {
			Class<T> clazz = findClass(className);
			impls.add(createInstance(clazz));
		}
		return impls;
	}

	/**
	 * @param contract the contract to test if an implementation has been defined
	 * @return true if an implementation is available
	 */
	public static boolean hasImplementation(final Class<?> contract) {
		return hasImplementation(contract.getName());
	}

	/**
	 * @param keySuffix the parameter key suffix to test if an implementation has been defined
	 * @return true if an implementation is available
	 */
	public static boolean hasImplementation(final String keySuffix) {
		return getImplClassName(keySuffix) != null;
	}

	/**
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
	 * @param suffixKey the parameter key suffix
	 * @return the implementing class name
	 */
	private static String getImplClassName(final String suffixKey) {
		String paramKey = getParamKey(suffixKey);
		return Config.getInstance().getString(paramKey);
	}

	/**
	 *
	 * @param suffixKey the parameter key suffix
	 * @return the implementing class names
	 */
	private static String[] getMultiImplClassName(final String suffixKey) {
		String paramKey = getParamKey(suffixKey);
		return Config.getInstance().getStringArray(paramKey);
	}

	/**
	 *
	 * @param suffixKey the parameter key suffix
	 * @return the fully qualified parameter key
	 */
	private static String getParamKey(final String suffixKey) {
		return PREFIX + suffixKey;
	}

}
