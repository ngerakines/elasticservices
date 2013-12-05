package com.socklabs.elasticservices.examples.calc.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import org.joda.time.DateTime;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.message.MessageUtils;
import com.socklabs.elasticservices.core.service.DefaultMessageController;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.examples.calc.CalcServiceProto;

import java.util.List;

public class CalcService implements Service {

	private final ServiceProto.ServiceRef serviceRef;

	private final ServiceRegistry serviceRegistry;

	private final MessageFactory calcMessageFactory;

	public CalcService(
			final ServiceProto.ServiceRef serviceRef, final ServiceRegistry serviceRegistry) {
		this.serviceRef = serviceRef;
		this.serviceRegistry = serviceRegistry;

		this.calcMessageFactory = new CalcMessageFactory();
	}

	@Override
	public ServiceProto.ServiceRef getServiceRef() {
		return serviceRef;
	}

	@Override
	public List<MessageFactory> getMessageFactories() {
		return ImmutableList.of(calcMessageFactory);
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		final Optional<DateTime> expiresOptional = controller.getExpires();
		if (expiresOptional.isPresent()) {
			final DateTime expires = expiresOptional.get();
			if (DateTime.now().isAfter(expires)) {
				return;
			}
		}
		if (message instanceof CalcServiceProto.Add) {
			int sum = 0;
			for (final Integer value : ((CalcServiceProto.Add) message).getValuesList()) {
				sum += value;
			}
			final CalcServiceProto.Result result = CalcServiceProto.Result.newBuilder().setValue(sum).build();

			final MessageController outboundController = new DefaultMessageController(
					serviceRef,
					controller.getSender(),
					ContentTypes.fromJsonClass(CalcServiceProto.Result.class),
					Optional.of(MessageUtils.randomMessageId(24)),
					controller.getMessageId());
			serviceRegistry.sendMessage(outboundController, result);
		}
	}

}
