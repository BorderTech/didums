package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * Unit tests for {@link Factory}.
 */
public class FactoryTest {

	private static final String PREFIX = "bordertech.factory.impl.";

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
		Assert.assertTrue("Should be an instanceof TestInterface", Factory.newInstance(
				TestFactoryInterface.class) instanceof TestFactoryInterface);
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
}
