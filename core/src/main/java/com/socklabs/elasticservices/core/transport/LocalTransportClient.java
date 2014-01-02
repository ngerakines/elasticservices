package com.socklabs.elasticservices.core.transport;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.message.MessageUtils;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ngerakines on 12/29/13.
 */
public class LocalTransportClient implements TransportClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalTransportClient.class);
	private static final BaseEncoding B16 = BaseEncoding.base16();

	private final Ref ref;
	private final Service service;
	private final String id;

	public LocalTransportClient(final Ref ref, final Service service) {
		this.ref = ref;
		this.service = service;
		this.id = B16.encode(MessageUtils.randomMessageId(14));
	}

	@Override
	public void send(final MessageController messageController, final AbstractMessage message) {
		LOGGER.debug("{} handling message as per {}", id, messageController);
		service.handleMessage(messageController, message);
	}

	@Override
	public Ref getRef() {
		return ref;
	}

}
