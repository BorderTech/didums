package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * Unit tests for {@link Didums}.
 */
public class DidumsTest {

	private static final String PREFIX = "bordertech.factory.impl.";

	@After
	public void restoreConfig() {
		Config.reset();
	}

	@Test
	public void testImplementationExists() {
		// Should not exist
		Assert.assertFalse("No implementation should exist", Didums.hasService(TestDidumsInterface.class));

		// Setup property
		final String key = PREFIX + TestDidumsInterface.class.getName();
		Config.getInstance().setProperty(key, TestDidumsInterfaceImpl.class.getName());

		// Should exist
		Assert.assertTrue("An implementation should exist", Didums.hasService(TestDidumsInterface.class));
	}

	@Test(expected = FactoryException.class)
	public void testNewInstanceNoImpl() {
		Factory.newInstance(TestDidumsInterface.class);
	}

	@Test
	public void testNewInstanceWithImpl() {
		Config.getInstance().setProperty(PREFIX + TestDidumsInterface.class.getName(), TestDidumsInterfaceImpl.class.getName());
		Assert.assertTrue("Should be an instanceof TestInterface", Didums.getService(
				TestDidumsInterface.class) instanceof TestDidumsInterface);
	}

	/**
	 * A test interface to use with the factory.
	 */
	public interface TestDidumsInterface {
	}

	/**
	 * An implementation of the test interface.
	 */
	public static final class TestDidumsInterfaceImpl implements TestDidumsInterface {
	}
}
