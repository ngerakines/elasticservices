package com.socklabs.elasticservices.examples.calc;

import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.edge.AbstractEdgeService;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.message.ResponseManager;
import com.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

public class CalcEdgeService extends AbstractEdgeService {

	public CalcEdgeService(
			final ServiceProto.ServiceRef serviceRef,
			final ResponseManager responseManager,
			final List<MessageFactory> messageFactories) {
		super(serviceRef, responseManager, messageFactories);
	}

	@Override
	protected boolean isResponse(MessageController controller, Message message) {
		return true;
	}

}
