package com.socklabs.elasticservices.core.transport;

import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.MessageController;

/**
 * A propagation mechanism for messages.
 */
public interface TransportClient {

	/** Attempts to deliver a message to the destination defined within a message controller. */
	void send(final MessageController messageController, final AbstractMessage message);

	Ref getRef();

}
