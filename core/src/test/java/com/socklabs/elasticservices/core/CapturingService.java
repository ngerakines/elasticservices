package com.socklabs.elasticservices.core;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

/**
 * Created by ngerakines on 1/2/14.
 */
public class CapturingService extends AbstractService {

	private final List<MessageFactory> messageFactories;
	private final List<Message> messages;

	protected CapturingService(final ServiceProto.ServiceRef serviceRef, final List<MessageFactory> messageFactories) {
		super(serviceRef);

		this.messageFactories = messageFactories;
		this.messages = Lists.newArrayList();
	}

	@Override
	public List<MessageFactory> getMessageFactories() {
		return messageFactories;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (messageHasExpired(controller)) {
			return;
		}
		messages.add(message);
	}

	public Optional<Message> getMessage(final int position) {
		if (messages.size() >= position) {
			return Optional.of(messages.get(position));
		}
		return Optional.absent();
	}

	public int getMessageCount() {
		return messages.size();
	}

}
