package com.github.bordertech.didums;

import com.github.bordertech.config.Config;

/**
 * Didums service implementation binding.
 * <p>
 * The initial bindings can be setup by implementing this interface and setting the following factory property.
 * </p>
 * <pre>
 * bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder=my.didums.DidumsBinder1
 * bordertech.factory.impl.com.github.bordertech.didums.DidumsBinder+=my.didums.DidumsBinder2
 * </pre>
 * <p>
 * As the example above shows, multiple <code>DidumsBinder</code> implementations can be set.
 * </p>
 *
 * @author Jonathan Austin
 *
 * @see Config
 * @see Didums
 *
 * @since 1.0.0
 */
public interface DidumsBinder {

	/**
	 * Configure the bindings.
	 *
	 * @param provider the provider to configure bindings on
	 */
	void configBindings(final DidumsProvider provider);
}
