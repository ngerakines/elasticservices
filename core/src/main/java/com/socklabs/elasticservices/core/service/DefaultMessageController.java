package com.socklabs.elasticservices.core.service;

import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.ServiceProto;

public class DefaultMessageController implements MessageController {

	private final ServiceProto.ServiceRef senderServiceRef;
	private final ServiceProto.ServiceRef destinationServiceRef;
	private final ServiceProto.ContentType contentType;
	private final Optional<byte[]> messageId;
	private final Optional<byte[]> correlationId;
	private final Optional<DateTime> expires;
	private final Optional<String> method;

	public DefaultMessageController(
			final ServiceProto.ServiceRef senderServiceRef,
			final ServiceProto.ServiceRef destinationServiceRef,
			final ServiceProto.ContentType contentType) {
		this(senderServiceRef, destinationServiceRef, contentType, Optional.<byte[]> absent(), Optional
				.<byte[]> absent());
	}

	public DefaultMessageController(
			final ServiceProto.ServiceRef senderServiceRef,
			final ServiceProto.ServiceRef destinationServiceRef,
			final ServiceProto.ContentType contentType,
			final Optional<byte[]> messageId,
			final Optional<byte[]> correlationId) {
		this(senderServiceRef, destinationServiceRef, contentType, messageId, correlationId, Optional
				.<DateTime> absent());
	}

	public DefaultMessageController(
			final ServiceProto.ServiceRef senderServiceRef,
			final ServiceProto.ServiceRef destinationServiceRef,
			final ServiceProto.ContentType contentType,
			final Optional<byte[]> messageId,
			final Optional<byte[]> correlationId,
			final Optional<DateTime> expires) {
		this(senderServiceRef, destinationServiceRef, contentType, messageId, correlationId, expires, Optional
				.<String> absent());
	}

	public DefaultMessageController(
			final ServiceProto.ServiceRef senderServiceRef,
			final ServiceProto.ServiceRef destinationServiceRef,
			final ServiceProto.ContentType contentType,
			final Optional<byte[]> messageId,
			final Optional<byte[]> correlationId,
			final Optional<DateTime> expires,
			final Optional<String> method) {
		this.senderServiceRef = senderServiceRef;
		this.destinationServiceRef = destinationServiceRef;
		this.contentType = contentType;
		this.messageId = messageId;
		this.correlationId = correlationId;
		this.expires = expires;
		this.method = method;
	}

	@Override
	public ServiceProto.ServiceRef getDestination() {
		return destinationServiceRef;
	}

	@Override
	public ServiceProto.ServiceRef getSender() {
		return senderServiceRef;
	}

	@Override
	public ServiceProto.ContentType getContentType() {
		return contentType;
	}

	@Override
	public Optional<byte[]> getMessageId() {
		return messageId;
	}

	@Override
	public Optional<byte[]> getCorrelationId() {
		return correlationId;
	}

	@Override
	public Optional<DateTime> getExpires() {
		return expires;
	}

	@Override
	public Optional<String> getMethod() {
		return method;
	}

	@Override
	public MessageController mutateWithMethod(final String method) {
		return new DefaultMessageController(
				senderServiceRef,
				destinationServiceRef,
				contentType,
				messageId,
				correlationId,
				expires,
				Optional.of(method));
	}

	@Override
	public String toString() {
		return "DefaultMessageController{" + "senderServiceRef=" + senderServiceRef + ", destinationServiceRef=" + destinationServiceRef + ", contentType=" + contentType + ", messageId=" + messageId + ", correlationId=" + correlationId + ", expires=" + expires + '}';
	}

}
