package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link Factory}.
 */
public class FactoryTest {

	private static final String PREFIX = "bordertech.factory.impl.";

	private static final String QUALIFIER = "A";

	@After
	public void restoreConfig() {
		Config.reset();
	}

	@Test
	public void testImplementationExists() {
		// Should not exist
		Assert.assertFalse("No implementation should exist", Factory.hasImplementation(TestFactoryInterface.class));
		// Setup property
		final String key = PREFIX + TestFactoryInterface.class.getName();
		Config.getInstance().setProperty(key, TestFactoryInterfaceImpl.class.getName());
		// Should exist
		Assert.assertTrue("An implementation should exist", Factory.hasImplementation(TestFactoryInterface.class));
	}

	@Test(expected = FactoryException.class)
	public void testNewInstanceNoImpl() {
		Factory.newInstance(TestFactoryInterface.class);
	}

	@Test
	public void testNewInstanceWithImpl() {
		Config.getInstance().setProperty(PREFIX + TestFactoryInterface.class.getName(), TestFactoryInterfaceImpl.class.getName());
		TestFactoryInterface impl = Factory.newInstance(TestFactoryInterface.class);
		Assert.assertTrue("Should be an instanceof TestInterface", impl instanceof TestFactoryInterface);
	}

	@Test
	public void testImplementationExistsAndQaulifier() {
		// Should not exist
		Assert.assertFalse("No implementation should exist for qualifier", Factory.hasImplementation(TestFactoryInterface.class, QUALIFIER));
		// Setup property
		final String key = PREFIX + TestFactoryInterface.class.getName() + "." + QUALIFIER;
		Config.getInstance().setProperty(key, TestFactoryInterfaceImpl2.class.getName());
		// Should exist
		Assert.assertTrue("An implementation with qualifier should exist", Factory.hasImplementation(TestFactoryInterface.class, QUALIFIER));
	}

	@Test(expected = FactoryException.class)
	public void testNewInstanceNoImplQualifier() {
		Factory.newInstance(TestFactoryInterface.class, QUALIFIER);
	}

	@Test
	public void testNewInstanceWithImplQualifier() {
		String key = PREFIX + TestFactoryInterface.class.getName() + "." + QUALIFIER;
		Config.getInstance().setProperty(key, TestFactoryInterfaceImpl2.class.getName());
		TestFactoryInterface impl = Factory.newInstance(TestFactoryInterface.class, QUALIFIER);
		Assert.assertTrue("Should be an instanceof TestFactoryInterfaceImpl2", impl instanceof TestFactoryInterfaceImpl2);
	}

	@Test
	public void testNewInstanceDefaultImpl() {
		TestFactoryInterface impl = Factory.newInstance(TestFactoryInterface.class, TestFactoryInterfaceImpl.class);
		Assert.assertTrue("Should be an instanceof the defualt impl", impl instanceof TestFactoryInterfaceImpl);
	}

	/**
	 * A test interface to use with the factory.
	 */
	public interface TestFactoryInterface {
	}

	/**
	 * An implementation of the test interface.
	 */
	public static final class TestFactoryInterfaceImpl implements TestFactoryInterface {
	}

	/**
	 * A second implementation of the test interface.
	 */
	public static final class TestFactoryInterfaceImpl2 implements TestFactoryInterface {
	}

}
