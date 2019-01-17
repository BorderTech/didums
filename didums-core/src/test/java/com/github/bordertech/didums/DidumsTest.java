package com.github.bordertech.didums;

import com.github.bordertech.config.Config;
import java.lang.annotation.Annotation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link Didums}.
 */
public class DidumsTest {

	private static final String PREFIX = "bordertech.factory.impl.";

	private static final TestAnnotatation QUALIFIER = new TestAnnotatation();

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
		TestDidumsInterface impl = Didums.getService(TestDidumsInterface.class);
		Assert.assertTrue("Should be an instanceof TestInterface", impl instanceof TestDidumsInterface);
	}

	@Test
	public void testImplementationExistsAndQaulifier() {
		// Should not exist
		Assert.assertFalse("No implementation should exist for qualifier", Didums.hasService(TestDidumsInterface.class, QUALIFIER));
		// Setup property
		final String key = PREFIX + TestDidumsInterface.class.getName() + "." + QUALIFIER;
		Config.getInstance().setProperty(key, TestDidumsInterfaceImpl2.class.getName());
		// Should exist
		Assert.assertTrue("An implementation with qualifier should exist", Didums.hasService(TestDidumsInterface.class, QUALIFIER));
	}

	@Test(expected = FactoryException.class)
	public void testNewInstanceNoImplQualifier() {
		Didums.getService(TestDidumsInterface.class, QUALIFIER);
	}

	@Test
	public void testNewInstanceWithImplQualifier() {
		String key = PREFIX + TestDidumsInterface.class.getName() + "." + QUALIFIER;
		Config.getInstance().setProperty(key, TestDidumsInterfaceImpl2.class.getName());
		TestDidumsInterface impl = Didums.getService(TestDidumsInterface.class, QUALIFIER);
		Assert.assertTrue("Should be an instanceof TestFactoryInterfaceImpl2", impl instanceof TestDidumsInterfaceImpl2);
	}

	@Test
	public void testNewInstanceDefaultImpl() {
		TestDidumsInterface impl = Didums.getService(TestDidumsInterface.class, TestDidumsInterfaceImpl.class);
		Assert.assertTrue("Should be an instanceof the default impl", impl instanceof TestDidumsInterfaceImpl);
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

	/**
	 * A second implementation of the test interface.
	 */
	public static final class TestDidumsInterfaceImpl2 implements TestDidumsInterface {
	}

	/**
	 * A test annotation for qualifier.
	 */
	public static final class TestAnnotatation implements Annotation {

		@Override
		public Class<? extends Annotation> annotationType() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String toString() {
			return "A";
		}
	}

}
