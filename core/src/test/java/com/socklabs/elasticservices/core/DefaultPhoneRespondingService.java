package com.socklabs.elasticservices.core;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ngerakines on 2/26/14.
 */
public class DefaultPhoneRespondingService extends AbstractService implements PhoneRespondingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPhoneRespondingService.class);

	private final ServiceRegistry serviceRegistry;

	public DefaultPhoneRespondingService(
			final ServiceProto.ServiceRef serviceRef,
			final List<MessageFactory> messageFactories,
			final ServiceRegistry serviceRegistry) {
		super(serviceRef, messageFactories);
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		final Optional<String> methodOptional = controller.getMethod();
		try {
			if (methodOptional.isPresent() && "call".equals(methodOptional.get()) && message instanceof TestProto.Foo) {
				final ListenableFuture<TestProto.Bar> responseFuture = call((TestProto.Foo) message);
				// NKG: The downside to using this method is that there is no
				// timeout associated with the `future.get()` method call.
				Futures.addCallback(responseFuture, new Callback<TestProto.Bar>(
						serviceRegistry,
						controller,
						getServiceRef(),
						ContentTypes.fromClass(TestProto.Bar.class)));
			} else {
				serviceRegistry.reply(controller, getServiceRef(), ServiceProto.EncodedError
						.newBuilder()
						.setCode("cool")
						.build(), ContentTypes.fromClass(ServiceProto.EncodedError.class));
			}
		} catch (final Exception e) {
			LOGGER.error("Exception caught.", e);
			serviceRegistry.reply(
					controller,
					getServiceRef(),
					ServiceProto.EncodedError.newBuilder().setCode(e.getMessage()).build(),
					ContentTypes.fromClass(ServiceProto.EncodedError.class));
		}
	}

	@Override
	public ListenableFuture<TestProto.Bar> call(final TestProto.Foo foo) {
		final SettableFuture<TestProto.Bar> future = SettableFuture.create();
		future.set(TestProto.Bar.newBuilder().addBazi(TestProto.Baz.newBuilder().setValue("ok")).build());
		return future;
	}

	private static class Callback<T extends AbstractMessage> implements FutureCallback<T> {

		private final ServiceRegistry serviceRegistry;
		private final MessageController messageController;
		private final ServiceProto.ServiceRef serviceRef;
		private final ServiceProto.ContentType contentType;

		private Callback(
				final ServiceRegistry serviceRegistry,
				final MessageController messageController,
				final ServiceProto.ServiceRef serviceRef,
				final ServiceProto.ContentType contentType) {
			this.contentType = contentType;
			this.messageController = messageController;
			this.serviceRef = serviceRef;
			this.serviceRegistry = serviceRegistry;
		}

		@Override
		public void onSuccess(final T result) {
			serviceRegistry.reply(messageController, serviceRef, result, contentType);
		}

		@Override
		public void onFailure(final Throwable t) {
			serviceRegistry.reply(
					messageController,
					serviceRef,
					ServiceProto.EncodedError.newBuilder().setCode(t.getMessage()).build(),
					ContentTypes.fromClass(ServiceProto.EncodedError.class));
		}

	}

}
