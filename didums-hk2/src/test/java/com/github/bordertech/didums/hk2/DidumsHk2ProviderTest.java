package com.github.bordertech.didums.hk2;

import com.github.bordertech.didums.DidumsProvider;
import com.github.bordertech.didums.Factory;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DidumsHk2Provider}.
 */
public class DidumsHk2ProviderTest {

	private DidumsHk2Provider provider;

	@Before
	public void setupProvider() {
		provider = new DidumsHk2Provider();
	}

	@After
	public void tearDown() {
		provider.getServiceLocator().shutdown();
	}

	@Test
	public void checkFactorySetting() {
		DidumsProvider impl = Factory.newInstance(DidumsProvider.class);
		Assert.assertTrue("Invalid provider instance via factory", impl instanceof DidumsHk2Provider);
	}

	@Test
	public void hk2ServiceNotExist() {
		Assert.assertNull("Service impl should not exist", provider.getService(TestHK2Interface.class));
	}

	@Test
	public void hk2ServiceExists() {
		// Bind Implementation
		provider.bind(TestHK2Interface.class, TestHK2Impl.class, false);
		Assert.assertTrue("Service impl should exist", provider.getService(TestHK2Interface.class) instanceof TestHK2Impl);
	}

	@Test
	public void hk2ServiceExistsQualified() {
		// BIND without qualifier
		provider.bind(TestHK2Interface.class, TestHK2Impl.class, false);
		// BIND with qualifier
		provider.bind(TestHK2Interface.class, TestHK2Impl2.class, false, new TestQualifierImpl());
		// Get instance of qualified class
		Assert.assertTrue("Service impl should be with the qualifier", provider.getService(TestHK2Interface.class, new TestQualifierImpl()) instanceof TestHK2Impl2);
	}

	@Test
	public void hk2TestInject() {
		// Bind Implementation to be injected
		provider.bind(TestHK2Interface.class, TestHK2Impl.class, false);
		// Create instance of class with injected class
		TestInject impl = provider.createAndInject(TestInject.class);
		// Check instance of injected class
		Assert.assertTrue("Invalid injected class", impl.foo instanceof TestHK2Impl);
	}

	/**
	 * A test interface to use with the factory.
	 */
	public static interface TestHK2Interface {
	}

	/**
	 * An implementation of the test interface.
	 */
	public static final class TestHK2Impl implements TestHK2Interface {
	}

	/**
	 * A second implementation of the test interface.
	 */
	public static final class TestHK2Impl2 implements TestHK2Interface {
	}

	/**
	 * Test class with injected interface.
	 */
	public static final class TestInject {

		@Inject
		private TestHK2Interface foo;
	}

}
