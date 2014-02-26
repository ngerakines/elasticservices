package com.socklabs.elasticservices.core.service;

import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.message.ResponseManager;

import java.util.List;

/**
 * Created by ngerakines on 2/26/14.
 */
public class ResponseService extends AbstractService {

	private final ResponseManager responseManager;

	public ResponseService(
			final ServiceProto.ServiceRef serviceRef,
			final List<MessageFactory> messageFactories,
			final ResponseManager responseManager) {
		super(serviceRef, messageFactories);
		this.responseManager = responseManager;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		responseManager.handleMessage(controller, message);
	}

}
