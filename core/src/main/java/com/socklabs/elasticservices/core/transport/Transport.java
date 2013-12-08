package com.socklabs.elasticservices.core.transport;

import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.MessageController;

/**
 * A delivery mechanism for messages. Transports use callbacks via added
 * {@link TransportConsumer} implementations to delivery messages locally.
 */
public interface Transport {

	/** Attempts to deliver a message to the destination defined within a message controller. */
	void send(final MessageController messageController, final AbstractMessage message);

	/** Add a local transport consumer to receive any messages sent to the transport. */
	void addConsumer(final TransportConsumer consumer);

	Ref getRef();

}
