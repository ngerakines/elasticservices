package com.socklabs.elasticservices.examples.calc.service;

import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.examples.calc.CalcServiceProto;

import java.util.List;

public class CalcService extends AbstractService {

	private final ServiceRegistry serviceRegistry;

	public CalcService(
			final List<MessageFactory> messageFactories,
			final ServiceProto.ServiceRef serviceRef,
			final ServiceRegistry serviceRegistry) {
		super(serviceRef, messageFactories);
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (messageHasExpired(controller)) {
			return;
		}
		if (message instanceof CalcServiceProto.Subtract) {
			final CalcServiceProto.Subtract subtractMessage = (CalcServiceProto.Subtract) message;
			int result = 0;
			if (subtractMessage.getValuesCount() > 0) {
				result = subtractMessage.getValues(0);
				if (subtractMessage.getValuesCount() > 1) {
					for (int i = 1; i < subtractMessage.getValuesCount(); i++) {
						result -= subtractMessage.getValues(i);
					}
				}
			}
			final CalcServiceProto.Result resultMessage =
					CalcServiceProto.Result.newBuilder().setValue(result).build();
			serviceRegistry.reply(
					controller,
					getServiceRef(),
					resultMessage,
					ContentTypes.fromClass(CalcServiceProto.Result.class));
		}
		if (message instanceof CalcServiceProto.Add) {
			int sum = 0;
			for (final Integer value : ((CalcServiceProto.Add) message).getValuesList()) {
				sum += value;
			}
			final CalcServiceProto.Result result =
					CalcServiceProto.Result.newBuilder().setValue(sum).build();
			serviceRegistry.reply(
					controller,
					getServiceRef(),
					result,
					ContentTypes.fromClass(CalcServiceProto.Result.class));
		}
	}

}
