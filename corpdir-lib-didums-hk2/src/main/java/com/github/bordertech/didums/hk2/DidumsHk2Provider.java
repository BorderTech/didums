package com.github.bordertech.didums.hk2;

import com.github.bordertech.config.Config;
import com.github.bordertech.didums.DidumsProvider;
import java.lang.annotation.Annotation;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;

/**
 * Didums Provider that uses HK2 for binding.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class DidumsHk2Provider implements DidumsProvider {

	/**
	 * Default HK2 context name.
	 */
	public static final String CONTEXT_NAME = Config.getInstance().getString("bordertech.didums.hk2.context", "bt-hk2");

	private final ServiceLocator serviceLocator;

	/**
	 * Use the default HK2 service locator.
	 */
	public DidumsHk2Provider() {
		ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
		serviceLocator = factory.create(CONTEXT_NAME);
	}

	/**
	 * Use a predefined HK2 service locator.
	 *
	 * @param serviceLocator the HK2 service locator to use
	 */
	public DidumsHk2Provider(final ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	/**
	 * @return the HK2 service locator
	 */
	public final ServiceLocator getServiceLocator() {
		return serviceLocator;
	}

	@Override
	public <T> T getService(final Class<T> service, final Annotation... qualifiers) {
		return serviceLocator.getService(service, qualifiers);
	}

	@Override
	public <T> T createAndInject(final Class<T> createMe) {
		return serviceLocator.createAndInitialize(createMe);
	}

	@Override
	public <T, U extends T> void bind(final Class<T> contract, final Class<U> contractImpl, final boolean singleton, final Annotation... qualifiers) {
		Binder binder = new AbstractBinder() {
			@Override
			protected void configure() {
				ServiceBindingBuilder builder = bind(contractImpl).to(contract);
				for (Annotation annotation : qualifiers) {
					builder.qualifiedBy(annotation);
				}
				if (singleton) {
					builder.in(Singleton.class);
				}
			}
		};
		ServiceLocatorUtilities.bind(serviceLocator, binder);
	}

}
