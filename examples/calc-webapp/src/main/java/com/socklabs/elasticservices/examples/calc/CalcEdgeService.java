package com.socklabs.elasticservices.examples.calc;

import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.edge.AbstractEdgeService;
import com.socklabs.elasticservices.core.edge.EdgeManager;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

public class CalcEdgeService extends AbstractEdgeService {

	public CalcEdgeService(
			final ServiceProto.ServiceRef serviceRef,
			final EdgeManager edgeManager,
			final List<MessageFactory> messageFactories) {
		super(serviceRef, edgeManager, messageFactories);
	}

	@Override
	protected boolean canHandleMessage(MessageController controller, Message message) {
		return true;
	}

}
