package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.misc.Ref;

/**
 * An interface that describes an object that may be able to provide a
 * {@link TransportClient} for a given {@link Ref}.
 */
public interface TransportClientFactory {

	/**
	 * Attempt to return {@link TransportClient} for a given {@link Ref}.
	 */
	Optional<TransportClient> get(Ref ref);

}
