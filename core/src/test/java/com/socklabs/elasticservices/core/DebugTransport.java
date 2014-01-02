package com.socklabs.elasticservices.core;

import com.google.common.collect.Lists;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.transport.TransportConsumer;

import java.util.List;

/**
 * Created by ngerakines on 1/2/14.
 */
public class DebugTransport implements Transport {

	private final List<TransportConsumer> consumers;
	private final Ref transportRef;

	public DebugTransport(final Ref transportRef) {
		this.transportRef = transportRef;
		this.consumers = Lists.newArrayList();
	}

	@Override
	public void addConsumer(final TransportConsumer consumer) {
		consumers.add(consumer);
	}

	@Override
	public Ref getRef() {
		return transportRef;
	}

	public void dispatch(final MessageController messageController, final byte[] payload) {
		for (final TransportConsumer transportConsumer : consumers) {
			transportConsumer.handleMessage(messageController, payload);
		}
	}

}
