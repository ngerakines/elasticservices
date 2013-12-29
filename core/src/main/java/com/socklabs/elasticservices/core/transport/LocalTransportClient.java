package com.socklabs.elasticservices.core.transport;

import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.Service;

/**
 * Created by ngerakines on 12/29/13.
 */
public class LocalTransportClient implements TransportClient {

	private final Ref ref;
	private final Service service;

	public LocalTransportClient(final Ref ref, final Service service) {
		this.ref = ref;
		this.service = service;
	}

	@Override
	public void send(final MessageController messageController, final AbstractMessage message) {
		service.handleMessage(messageController, message);
	}

	@Override
	public Ref getRef() {
		return ref;
	}

}
