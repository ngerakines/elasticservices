package com.socklabs.elasticservices.core.transport;

import com.socklabs.elasticservices.core.misc.Ref;

/**
 * A delivery mechanism for messages. Transports use callbacks via added
 * {@link TransportConsumer} implementations to delivery messages locally.
 */
public interface Transport {

	/** Add a local transport consumer to receive any messages sent to the transport. */
	void addConsumer(final TransportConsumer consumer);

	Ref getRef();

}
