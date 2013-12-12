package com.socklabs.elasticservices.http;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Longs;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageUtils;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.DefaultMessageController;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.transport.TransportConsumer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by ngerakines on 12/12/13.
 */
@Controller
public class HttpTransportController implements Transport {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransportController.class);
	private static final BaseEncoding B16 = BaseEncoding.base16();

	private final ServiceRegistry serviceRegistry;
	private final Ref transportRef;
	private final List<TransportConsumer> consumers;

	public HttpTransportController(final ServiceRegistry serviceRegistry, final Ref transportRef) {
		this.serviceRegistry = serviceRegistry;
		this.transportRef = transportRef;
		this.consumers = Lists.newArrayList();
	}

	@Override
	public void addConsumer(final TransportConsumer consumer) {
		consumers.add(consumer);
	}

	@Override
	public Ref getRef() {
		return transportRef;
	}

	@ResponseBody
	@RequestMapping(value = "/{service}", method = RequestMethod.POST, produces = {"text/plain"})
	public String handleRequest(
			final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse,
			final Model model,
			@PathVariable("service") final String serviceName,
			@PathVariable("method") final String methodName,
			@RequestParam(value = "json", required = false) @Nullable final String json) throws TransportException {

		final Optional<byte[]> rawMessageOptional = readBody(httpServletRequest);
		if (!rawMessageOptional.isPresent()) {
			throw new TransportException("Could not read raw message or none provided.");
		}

		final MessageController messageController = buildMessageController(httpServletRequest);

		for (final TransportConsumer transportConsumer : consumers) {
			try {
				transportConsumer.handleMessage(messageController, rawMessageOptional.get());
			} catch (final Exception e) {
				LOGGER.error("Error giving message to transport consumer:", e);
			}
		}

		return "OK";
	}

	@ExceptionHandler(TransportException.class)
	public ModelAndView handleCustomException(final TransportException ex) {

		final ModelAndView model = new ModelAndView(new JsonView());
		model.addObject("message", ex.getMessage());

		return model;

	}

	private Optional<byte[]> readBody(final HttpServletRequest httpServletRequest) {
		try {
			final byte[] messageBody = ByteStreams.toByteArray(httpServletRequest.getInputStream());
			if (messageBody != null) {
				return Optional.of(messageBody);
			}
		} catch (final IOException e) {
			LOGGER.error("Could not read request body.", e);
		}
		return Optional.absent();
	}

	private MessageController buildMessageController(final HttpServletRequest httpServletRequest) {
		final String rawContentType = httpServletRequest.getContentType();
		final Optional<Message> contentType = MessageUtils.fromJson(
				ServiceProto.ContentType.getDefaultInstance(),
				rawContentType);

		final String rawDestinationServiceRef = httpServletRequest.getHeader("X-DESTINATION");
		final Optional<Message> destinationServiceRef = MessageUtils.fromJson(
				ServiceProto.ServiceRef.getDefaultInstance(),
				rawDestinationServiceRef);

		final String rawSenderServiceRef = httpServletRequest.getHeader("X-SENDER");
		final Optional<Message> senderServiceRef = MessageUtils.fromJson(
				ServiceProto.ServiceRef.getDefaultInstance(),
				rawSenderServiceRef);

		Optional<byte[]> optionalMessageId = Optional.absent();
		Optional<byte[]> optionalCorrelationId = Optional.absent();
		Optional<DateTime> expiresOptional = Optional.absent();

		final String messageId = httpServletRequest.getHeader("X-MESSAGE-ID");
		if (messageId != null && !messageId.isEmpty()) {
			optionalMessageId = Optional.of(B16.decode(messageId));
		}

		final String correlationId = httpServletRequest.getHeader("X-CORRELATION-ID");
		if (correlationId != null && !correlationId.isEmpty()) {
			optionalCorrelationId = Optional.of(B16.decode(correlationId));
		}

		final String rawExpires = httpServletRequest.getHeader("X-CORRELATION-ID");
		if (rawExpires != null && !rawExpires.isEmpty()) {
			final Long millis = Longs.tryParse(rawExpires);
			if (millis != null) {
				final DateTime expires = new DateTime(millis);
				expiresOptional = Optional.of(expires);
			}
		}

		return new DefaultMessageController(
				(ServiceProto.ServiceRef) senderServiceRef.get(),
				(ServiceProto.ServiceRef) destinationServiceRef.get(),
				(ServiceProto.ContentType) contentType.get(),
				optionalMessageId,
				optionalCorrelationId,
				expiresOptional);
	}

	private class TransportException extends Throwable {

		private TransportException(final String message, final Throwable cause) {
			super(message, cause);
		}

		private TransportException(final String message) {
			super(message);
		}

	}

}
