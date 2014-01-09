package com.socklabs.elasticservices.core;

import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

/**
 * Created by ngerakines on 1/2/14.
 */
public class ExplodingService extends AbstractService {

	protected ExplodingService(final ServiceProto.ServiceRef serviceRef, final List<MessageFactory> messageFactories) {
		super(serviceRef, messageFactories);
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (messageHasExpired(controller)) {
			return;
		}
		throw new RuntimeException("Boom baby!");
	}

}
