package com.socklabs.elasticservices.core.edge;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.Service;

import java.util.List;

public abstract class AbstractEdgeService implements Service {

	private final ServiceProto.ServiceRef serviceRef;
	private final EdgeManager edgeManager;
	private final ImmutableList<MessageFactory> messageFactories;

	public AbstractEdgeService(
			final ServiceProto.ServiceRef serviceRef,
			final EdgeManager edgeManager,
			final List<MessageFactory> messageFactories) {
		this.serviceRef = serviceRef;
		this.edgeManager = edgeManager;
		this.messageFactories = ImmutableList.copyOf(messageFactories);
	}

	@Override
	public ServiceProto.ServiceRef getServiceRef() {
		return serviceRef;
	}

	@Override
	public List<MessageFactory> getMessageFactories() {
		return messageFactories;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (canHandleMessage(controller, message)) {
			edgeManager.handleMessage(controller, message);
		}
	}

	protected abstract boolean canHandleMessage(final MessageController controller, final Message message);

}
