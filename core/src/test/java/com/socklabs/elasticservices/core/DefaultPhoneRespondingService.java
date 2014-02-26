package com.socklabs.elasticservices.core;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractRespondingService;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A responding service implementation that uses the abstract responding service.
 */
public class DefaultPhoneRespondingService extends AbstractRespondingService implements PhoneRespondingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPhoneRespondingService.class);

	public DefaultPhoneRespondingService(
			final ServiceProto.ServiceRef serviceRef,
			final List<MessageFactory> messageFactories,
			final ServiceRegistry serviceRegistry) {
		super(serviceRef, messageFactories, serviceRegistry);
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		final Optional<String> methodOptional = controller.getMethod();
		try {
			if (methodOptional.isPresent() && "call".equals(methodOptional.get()) && message instanceof TestProto.Foo) {
				final ListenableFuture<TestProto.Bar> responseFuture = call((TestProto.Foo) message);
				reply(responseFuture, controller, ContentTypes.fromClass(TestProto.Bar.class));
			} else {
				fail(controller, EsError.INVALID_SERVICE_METHOD);
			}
		} catch (final Exception e) {
			LOGGER.error("Exception caught.", e);
			fail(controller, EsError.SERVICE_EXECUTION_ERROR);
		}
	}

	@Override
	public ListenableFuture<TestProto.Bar> call(final TestProto.Foo foo) {
		final SettableFuture<TestProto.Bar> future = SettableFuture.create();
		future.set(TestProto.Bar.newBuilder().addBazi(TestProto.Baz.newBuilder().setValue("ok")).build());
		return future;
	}

}
