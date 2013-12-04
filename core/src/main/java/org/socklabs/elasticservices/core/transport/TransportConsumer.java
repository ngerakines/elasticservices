package org.socklabs.elasticservices.core.transport;

import org.socklabs.elasticservices.core.service.MessageController;

/**
 * An interface used to designate objects as receiving messages from transports.
 */
public interface TransportConsumer {

	void handleMessage(
			final MessageController messageController, final byte[] rawMessage);

}
