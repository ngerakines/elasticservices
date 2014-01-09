package com.socklabs.elasticservices.core.edge;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.message.ResponseManager;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

public abstract class AbstractEdgeService extends AbstractService {

	private final ResponseManager responseManager;

	public AbstractEdgeService(
			final ServiceProto.ServiceRef serviceRef,
			final ResponseManager responseManager,
			final List<MessageFactory> messageFactories) {
		super(serviceRef, messageFactories);
		this.responseManager = responseManager;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (isResponse(controller, message)) {
			responseManager.handleMessage(controller, message);
		}
	}

	protected abstract boolean isResponse(final MessageController controller, final Message message);

}
