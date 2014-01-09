package com.socklabs.elasticservices.activemq;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageUtils;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.DefaultMessageController;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.transport.TransportConsumer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import javax.annotation.PostConstruct;
import javax.jms.*;
import javax.jms.Message;
import java.util.List;

/**
 * Created by nick.gerakines on 1/9/14.
 */
public class ActiveMqTransport implements Transport, DisposableBean {

	private static final BaseEncoding B16 = BaseEncoding.base16();

	private final List<TransportConsumer> consumers;
	private final ActiveMqTransportRef activeMqTransportRef;
	private final Connection connection;
	private final Session session;

	public ActiveMqTransport(
			final ConnectionFactory connectionFactory,
			final ActiveMqTransportRef activeMqTransportRef) throws JMSException {
		this.consumers = Lists.newArrayList();
		this.activeMqTransportRef = activeMqTransportRef;

		this.connection = connectionFactory.createConnection();
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		final Destination destination = session.createQueue(activeMqTransportRef.getQueue());

		final MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(new ActiveMqTransportListener(consumers));
	}

	@Override
	public void addConsumer(final TransportConsumer consumer) {
		consumers.add(consumer);
	}

	@Override
	public Ref getRef() {
		return activeMqTransportRef.getRef();
	}

	@PostConstruct
	public void init() throws JMSException {
		connection.start();
	}

	@Override
	public void destroy() throws Exception {
		connection.stop();
		session.close();
		connection.close();
	}

	private static class ActiveMqTransportListener implements javax.jms.MessageListener {

		private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqTransportListener.class);
		private final List<TransportConsumer> transportConsumers;

		private ActiveMqTransportListener(final List<TransportConsumer> transportConsumers) {
			this.transportConsumers = transportConsumers;
		}

		@Override
		public void onMessage(final Message message) {
			final Optional<MessageController> messageControllerOptional = buildMessageController(message);
			if (!messageControllerOptional.isPresent()) {
				LOGGER.error("Could not parse message controller from message.");
				return;
			}
			final Optional<byte[]> bodyOptional = buildMessageBody(message);
			if (!bodyOptional.isPresent()) {
				LOGGER.error("Could not parse body from message.");
				return;
			}
			final MessageController messageController = messageControllerOptional.get();
			final byte[] body = bodyOptional.get();
			for (final TransportConsumer transportConsumer : transportConsumers) {
				try {
					transportConsumer.handleMessage(messageController, body);
				} catch (final Exception e) {
					LOGGER.error("Error giving message to transport consumer:", e);
				}
			}
		}

		private Optional<byte[]> buildMessageBody(final Message message) {
			if (message instanceof BytesMessage) {
				try {
					final long bodyLength = ((BytesMessage) message).getBodyLength();
					Preconditions.checkArgument(bodyLength <= Integer.MAX_VALUE);
					final byte[] body = new byte[(int) bodyLength];
					((BytesMessage) message).readBytes(body);
					return Optional.of(body);
				} catch (final JMSException e) {
					LOGGER.error("JMSException raised reading body from message.", e);
				}
			}
			return Optional.absent();
		}

		private Optional<MessageController> buildMessageController(final Message message) {
			try {
				final String rawContenType = message.getStringProperty("content-type");
				final Optional<com.google.protobuf.Message> contentType = MessageUtils.fromJson(
						ServiceProto.ContentType.getDefaultInstance(),
						rawContenType);

				final String rawDestinationServiceRef = message.getStringProperty("app-id");
				final Optional<com.google.protobuf.Message> destinationServiceRef = MessageUtils.fromJson(
						ServiceProto.ServiceRef.getDefaultInstance(),
						rawDestinationServiceRef);

				final String rawSenderServiceRef = message.getStringProperty("reply-to");
				final Optional<com.google.protobuf.Message> senderServiceRef = MessageUtils.fromJson(
						ServiceProto.ServiceRef.getDefaultInstance(),
						rawSenderServiceRef);

				Optional<byte[]> optionalMessageId = Optional.absent();
				Optional<byte[]> optionalCorrelationId = Optional.absent();

				final String messageId = message.getStringProperty("message-id");
				if (messageId != null && !messageId.isEmpty()) {
					optionalMessageId = Optional.of(B16.decode(messageId));
				}

				final String correlationId = message.getStringProperty("correlation-id");
				if (correlationId != null && !correlationId.isEmpty()) {
					optionalCorrelationId = Optional.of(B16.decode(correlationId));
				}

				Optional<DateTime> expiresOptional = Optional.absent();
				if (message.propertyExists("expires")) {
					final long millis = message.getLongProperty("expires");
					final DateTime expires = new DateTime(millis);
					expiresOptional = Optional.of(expires);
				}

				final MessageController messageController = new DefaultMessageController(
						(ServiceProto.ServiceRef) senderServiceRef.get(),
						(ServiceProto.ServiceRef) destinationServiceRef.get(),
						(ServiceProto.ContentType) contentType.get(),
						optionalMessageId,
						optionalCorrelationId,
						expiresOptional);
				return Optional.of(messageController);
			} catch (final JMSException e) {
				LOGGER.error("JMSException raised processing message headers.", e);
			}
			return Optional.absent();
		}

	}

}
