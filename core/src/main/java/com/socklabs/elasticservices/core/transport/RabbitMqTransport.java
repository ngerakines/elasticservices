package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.BasicCounter;
import com.netflix.servo.monitor.BasicGauge;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.MonitorConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageUtils;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.DefaultMessageController;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.servo.ext.CollectionSizeCallable;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RabbitMqTransport extends AbstractTransport {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqTransport.class);
	private static final BaseEncoding B16 = BaseEncoding.base16();
	private final Channel channel;
	private final List<TransportConsumer> consumers;
	private final RabbitMqTransportRef transportRef;

	public RabbitMqTransport(
			final Connection connection,
			final Ref transportRef) throws IOException {

		this.transportRef = new RabbitMqTransportRef(transportRef);
		this.consumers = Lists.newArrayList();

		final Counter deliveryCount = new BasicCounter(
				MonitorConfig.builder("deliveryCount").withTag(
						"transport",
						getRef().toString()).build());
		final Counter deliveryFailureCount = new BasicCounter(
				MonitorConfig.builder("deliveryFailureCount").withTag(
						"transport",
						getRef().toString()).build());
		DefaultMonitorRegistry.getInstance().register(deliveryCount);
		DefaultMonitorRegistry.getInstance().register(deliveryFailureCount);
		final MonitorConfig consumersSizeMonitorConfig = MonitorConfig.builder("consumers")
				.withTag("transport", getRef().toString())
				.build();
		DefaultMonitorRegistry.getInstance().register(
				new BasicGauge<>(
						consumersSizeMonitorConfig,
						new CollectionSizeCallable(this.consumers)));

		channel = connection.createChannel();

		final AMQP.Queue.DeclareOk queueDecl = channel.queueDeclare();
		LOGGER.info("queue declared: {}", queueDecl);

		final AMQP.Exchange.DeclareOk exchangeDeclOk = channel.exchangeDeclare(
				this.transportRef.getExchange(),
				this.transportRef.getType(),
				true,
				true,
				Maps.<String, Object>newHashMap());
		LOGGER.info("exchange declared: {}", exchangeDeclOk);

		final AMQP.Queue.BindOk queueBindOk = channel.queueBind(
				queueDecl.getQueue(),
				this.transportRef.getExchange(),
				"fanout".equals(this.transportRef.getType()) ? "" : this.transportRef.getRoutingKey());
		LOGGER.info("queue binding declared: {}", queueBindOk);

		final String consumerTag = channel.basicConsume(
				queueDecl.getQueue(),
				true,
				new FabricMessageConsumer(consumers, deliveryCount, deliveryFailureCount));
		LOGGER.info("Received consumer tag {}", consumerTag);
	}

	@Override
	public void send(final MessageController messageController, final AbstractMessage message) {

	}

	@Override
	public void addConsumer(final TransportConsumer consumer) {
		consumers.add(consumer);
	}

	@Override
	public Ref getRef() {
		return transportRef.getRef();
	}

	private static class FabricMessageConsumer implements Consumer {

		private static final Logger LOGGER = LoggerFactory.getLogger(FabricMessageConsumer.class);
		private final List<TransportConsumer> transportConsumers;
		private final Counter deliveryCount;
		private final Counter deliveryFailureCount;

		private FabricMessageConsumer(
				final List<TransportConsumer> transportConsumers,
				final Counter deliveryCount,
				final Counter deliveryFailureCount) {
			this.transportConsumers = transportConsumers;
			this.deliveryCount = deliveryCount;
			this.deliveryFailureCount = deliveryFailureCount;
		}

		@Override
		public void handleConsumeOk(final String consumerTag) {
		}

		@Override
		public void handleCancelOk(final String consumerTag) {
		}

		@Override
		public void handleCancel(final String consumerTag) throws IOException {
		}

		@Override
		public void handleShutdownSignal(final String consumerTag, final ShutdownSignalException sig) {
		}

		@Override
		public void handleRecoverOk(final String consumerTag) {
		}

		@Override
		public void handleDelivery(
				final String consumerTag,
				final Envelope envelope,
				final AMQP.BasicProperties properties,
				final byte[] body) throws IOException {
			deliveryCount.increment();
			final MessageController messageController = buildMessageController(properties);
			for (final TransportConsumer transportConsumer : transportConsumers) {
				try {
					transportConsumer.handleMessage(messageController, body);
				} catch (final Exception e) {
					deliveryFailureCount.increment();
					LOGGER.error("Error giving message to transport consumer:", e);
				}
			}
		}

		private MessageController buildMessageController(final BasicProperties properties) {
			final String rawContenType = properties.getContentType();
			final Optional<Message> contentType = MessageUtils.fromJson(
					ServiceProto.ContentType.getDefaultInstance(),
					rawContenType);

			final String rawDestinationServiceRef = properties.getAppId();
			final Optional<Message> destinationServiceRef = MessageUtils.fromJson(
					ServiceProto.ServiceRef.getDefaultInstance(),
					rawDestinationServiceRef);

			final String rawSenderServiceRef = properties.getReplyTo();
			final Optional<Message> senderServiceRef = MessageUtils.fromJson(
					ServiceProto.ServiceRef.getDefaultInstance(),
					rawSenderServiceRef);

			Optional<byte[]> optionalMessageId = Optional.absent();
			Optional<byte[]> optionalCorrelationId = Optional.absent();

			final String messageId = properties.getMessageId();
			if (messageId != null && !messageId.isEmpty()) {
				optionalMessageId = Optional.of(B16.decode(messageId));
			}

			final String correlationId = properties.getCorrelationId();
			if (correlationId != null && !correlationId.isEmpty()) {
				optionalCorrelationId = Optional.of(B16.decode(correlationId));
			}

			Optional<DateTime> expiresOptional = Optional.absent();
			final Map<String, Object> headers = properties.getHeaders();
			if (headers != null) {
				final Long millis = Longs.tryParse(headers.get("expires").toString());
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

	}

}

