package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.Expiration;
import com.socklabs.elasticservices.core.message.ResponseManager;

import java.lang.reflect.Method;

/**
 * Created by ngerakines on 2/19/14.
 */
public class RespondingServiceTransportClientProxyHandler<T> extends AbstractInvocationHandler {

	private final ResponseManager responseManager;
	private final ServiceProto.ServiceRef destination;

	public RespondingServiceTransportClientProxyHandler(
			final ResponseManager responseManager,
			final ServiceProto.ServiceRef destination) {
		this.responseManager = responseManager;
		this.destination = destination;
	}

	@Override
	protected Object handleInvocation(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.getReturnType() != ListenableFuture.class) {
			throw new UnsupportedOperationException();
		}

		final Optional<AbstractMessage> messageOptional = messageOptional(args);
		if (!messageOptional.isPresent()) {
			throw new UnsupportedOperationException();
		}
		final AbstractMessage message = messageOptional.get();

		return responseManager.sendAndReceive(
				destination,
				message,
				message.getClass(),
				Optional.<Expiration> absent(),
				Optional.of(method.getName()));
	}

	private Optional<AbstractMessage> messageOptional(final Object[] args) {
		for (final Object arg : args) {
			if (arg instanceof AbstractMessage) {
				return Optional.of((AbstractMessage) arg);
			}
		}
		return Optional.absent();
	}

}
