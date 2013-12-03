package org.socklabs.elasticservices.core.transport;

import com.google.protobuf.AbstractMessage;
import org.socklabs.elasticservices.core.service.MessageController;

/**
 * A delivery mechanism for messages. Transports are asynchronous and message
 * delivery is two ways. Transports use callbacks via added
 * {@link TransportConsumer} implementations to delivery messages locally.
 */
public interface Transport {

    /**
     * Attempts to deliver a message to the destination defined within a message controller.
     */
    void send(final MessageController messageController, final AbstractMessage message);

    /**
     * Add a local transport consumer to receive any messages sent to the transport.
     */
    void addConsumer(final TransportConsumer consumer);

    String getRef();

}
