package com.socklabs.elasticservices.core.service;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.error.EncodedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ngerakines on 2/26/14.
 */
public abstract class AbstractRespondingService extends AbstractService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRespondingService.class);

	private final ServiceRegistry serviceRegistry;

	public AbstractRespondingService(
			final ServiceProto.ServiceRef serviceRef,
			final List<MessageFactory> messageFactories,
			final ServiceRegistry serviceRegistry) {
		super(serviceRef, messageFactories);
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public abstract void handleMessage(final MessageController controller, final Message message);

	protected <T extends AbstractMessage> void reply(
			final ListenableFuture<T> responseFuture,
			final MessageController messageController,
			final ServiceProto.ContentType contentType) {
		// NKG: The downside to using this method is that there is no
		// timeout associated with the `future.get()` method call.
		Futures.addCallback(responseFuture, new Callback<T>(
				serviceRegistry,
				messageController,
				getServiceRef(),
				contentType));
	}

	protected void fail(final MessageController messageController, final Throwable throwable) {
		serviceRegistry.reply(
				messageController,
				getServiceRef(),
				ServiceProto.EncodedError.newBuilder().setCode(throwable.getMessage()).build(),
				ContentTypes.fromClass(ServiceProto.EncodedError.class));
	}

	protected void fail(final MessageController messageController, final EncodedError encodedError) {
		serviceRegistry.reply(
				messageController,
				getServiceRef(),
				ServiceProto.EncodedError.newBuilder().setCode(encodedError.code()).build(),
				ContentTypes.fromClass(ServiceProto.EncodedError.class));
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
