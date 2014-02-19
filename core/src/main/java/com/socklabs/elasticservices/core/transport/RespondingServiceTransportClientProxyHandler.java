package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.message.ResponseManager;
import com.socklabs.elasticservices.core.service.MessageController;

import java.lang.reflect.Method;

/**
 * Created by ngerakines on 2/19/14.
 */
public class RespondingServiceTransportClientProxyHandler<T> extends AbstractInvocationHandler {

	private final ResponseManager responseManager;

	public RespondingServiceTransportClientProxyHandler(
			final ResponseManager responseManager) {
		this.responseManager = responseManager;
	}

	@Override
	protected Object handleInvocation(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.getReturnType() != AbstractFuture.class) {
			throw new UnsupportedOperationException();
		}
		final Optional<MessageController> messageControllerOptional = messageControllerOptional(args);
		if (!messageControllerOptional.isPresent()) {
			throw new UnsupportedOperationException();
		}
		final MessageController messageController = messageControllerOptional.get();

		final Optional<AbstractMessage> messageOptional = messageOptional(args);
		if (!messageOptional.isPresent()) {
			throw new UnsupportedOperationException();
		}
		final AbstractMessage message = messageOptional.get();

		return responseManager.sendAndReceive(messageController, message);
	}

	private Optional<AbstractMessage> messageOptional(final Object[] args) {
		for (final Object arg : args) {
			if (arg instanceof AbstractMessage) {
				return Optional.of((AbstractMessage) arg);
			}
		}
		return Optional.absent();
	}

	private Optional<MessageController> messageControllerOptional(final Object[] args) {
		for (final Object arg : args) {
			if (arg instanceof MessageController) {
				return Optional.of((MessageController) arg);
			}
		}
		return Optional.absent();
	}

}
