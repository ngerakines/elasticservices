package com.socklabs.elasticservices.rabbitmq;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.AbstractMessage;
import com.googlecode.protobuf.format.JsonFormat;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.BasicCounter;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.MonitorConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.transport.TransportClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class RabbitMqTransportClient implements TransportClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqTransportClient.class);
	private static final BaseEncoding B16 = BaseEncoding.base16();

	private final RabbitMqTransportRef transportRef;
	private final Channel channel;
	private final Counter sendCount;
	private final Counter sendFailureCount;

	public RabbitMqTransportClient(final Ref ref, final Connection connection) throws IOException {
		this.channel = connection.createChannel();

		this.sendCount = new BasicCounter(
				MonitorConfig.builder("sendCount").withTag(
						"transport",
						ref.toString()).build());
		this.sendFailureCount = new BasicCounter(
				MonitorConfig.builder("sendFailureCount").withTag(
						"transport",
						ref.toString()).build());
		this.transportRef = new RabbitMqTransportRef(ref);
		DefaultMonitorRegistry.getInstance().register(this.sendCount);
		DefaultMonitorRegistry.getInstance().register(this.sendFailureCount);
	}

	@Override
	public void send(final MessageController messageController, final AbstractMessage message) {
		sendCount.increment();
		final ServiceProto.ContentType contentType = getContentType(messageController);
		if (contentType == null) {
			sendFailureCount.increment();
			throw new RuntimeException("Could not get content type of message.");
		}
		try {
			final byte[] messageBytes = rawMessageBytes(contentType, message);
			final AMQP.BasicProperties basicProperties = buildBasicProperties(messageController, contentType);
			channel.basicPublish(
					transportRef.getExchange(),
					"fanout".equals(transportRef.getType()) ? "" : transportRef.getRoutingKey(),
					basicProperties,
					messageBytes);
		} catch (final Exception e) {
			sendFailureCount.increment();
			LOGGER.error("Exception caught publishing message:", e);
		}
	}

	@Override
	public Ref getRef() {
		return transportRef.getRef();
	}

	private AMQP.BasicProperties buildBasicProperties(
			final MessageController messageController,
			final ServiceProto.ContentType contentType) {
		final AMQP.BasicProperties.Builder propertiesBuilder = new AMQP.BasicProperties.Builder();
		propertiesBuilder.contentType(JsonFormat.printToString(contentType));

		final ServiceProto.ServiceRef serviceRef = messageController.getDestination();
		propertiesBuilder.appId(JsonFormat.printToString(serviceRef));

		final ServiceProto.ServiceRef senderServiceRef = messageController.getSender();
		propertiesBuilder.replyTo(JsonFormat.printToString(senderServiceRef));

		final Optional<byte[]> optionalMessageId = messageController.getMessageId();
		if (optionalMessageId.isPresent()) {
			propertiesBuilder.messageId(B16.encode(optionalMessageId.get()));
		}

		final Optional<byte[]> optionalCorrelationId = messageController.getCorrelationId();
		if (optionalCorrelationId.isPresent()) {
			propertiesBuilder.correlationId(B16.encode(optionalCorrelationId.get()));
		}

		final Map<String, Object> headers = Maps.newHashMap();

		/*
		NKG: The "expiration" property is handled by the queue and using it
		could have side effects. For that reason, if the sender of a message
		wants to /hint/ that a message shouldn't be processed after a certain
		time, we set that as a header key/value.
		 */
		final Optional<DateTime> optionalExpires = messageController.getExpires();
		if (optionalExpires.isPresent()) {
			headers.put("expires", String.valueOf(optionalExpires.get().getMillis()));
		}

		if (headers.size() > 0) {
			propertiesBuilder.headers(headers);
		}

		return propertiesBuilder.build();
	}

	private ServiceProto.ContentType getContentType(
			final MessageController controller) {
		return controller.getContentType();
	}

	private <M extends com.google.protobuf.Message> byte[] rawMessageBytes(
			final ServiceProto.ContentType contentType,
			final M message) {
		if (contentType.getValue().equals(ContentTypes.CONTENT_TYPE_JSON)) {
			return JsonFormat.printToString(message).getBytes();
		}
		return message.toByteArray();
	}

}
