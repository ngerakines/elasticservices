package com.socklabs.elasticservices.examples.calc.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.examples.calc.CalcServiceProto;
import com.socklabs.feature.ToggleFeature;
import org.joda.time.DateTime;

import java.util.List;

public class CalcService extends AbstractService {

	private final ServiceRegistry serviceRegistry;
	private final ToggleFeature toggleFeature;

	private final MessageFactory calcMessageFactory;

	public CalcService(
			final ServiceProto.ServiceRef serviceRef,
			final ServiceRegistry serviceRegistry,
			final ToggleFeature toggleFeature) {
		super(serviceRef);
		this.serviceRegistry = serviceRegistry;
		this.toggleFeature = toggleFeature;
		this.calcMessageFactory = new CalcMessageFactory();
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
		if (message instanceof CalcServiceProto.Subtract && toggleFeature.isEnabled()) {
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
					ContentTypes.fromJsonClass(CalcServiceProto.Result.class));
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
					ContentTypes.fromJsonClass(CalcServiceProto.Result.class));
		}
	}

}
