package com.github.bordertech.didums;

import java.lang.annotation.Annotation;

/**
 * Default Provider.
 * <p>
 * The default implementation provides no DI functionality.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class DefaultDidumsProvider implements DidumsProvider {

	@Override
	public <T> T getService(final Class<T> service, final Annotation... qualifiers) {
		return null;
	}

	@Override
	public <T> T createAndInject(final Class<T> createMe) {
		throw new UnsupportedOperationException("Not supported by default provider.");
	}

	@Override
	public <T, U extends T> void bind(final Class<T> contract, final Class<U> contractImpl, final boolean singleton, final Annotation... qualifiers) {
		throw new UnsupportedOperationException("Not supported by default provider.");
	}

}
