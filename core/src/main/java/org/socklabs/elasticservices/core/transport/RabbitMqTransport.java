package org.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.MessageUtils;
import org.socklabs.elasticservices.core.service.DefaultMessageController;
import org.socklabs.elasticservices.core.service.MessageController;

import java.io.IOException;
import java.util.List;

public class RabbitMqTransport extends AbstractTransport {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqTransport.class);

    private final Channel channel;

    private final List<TransportConsumer> consumers;

    private final String exchange;

    private final String routingKey;

    private final String exchangeType;

    public RabbitMqTransport(
            final Connection connection,
            final String exchange,
            final String routingKey,
            final String exchangeType,
            final boolean forLocalServiceImpl
    ) throws IOException {

        this.exchange = exchange;
        this.routingKey = routingKey;
        this.exchangeType = exchangeType;
        channel = connection.createChannel();

        consumers = Lists.newArrayList();
        if (forLocalServiceImpl) {
            final AMQP.Queue.DeclareOk queueDecl = channel.queueDeclare();
            log.info("queue declared: {}", queueDecl);

            final AMQP.Exchange.DeclareOk exchangeDeclOk = channel.exchangeDeclare(exchange,
                    exchangeType,
                    true,
                    true,
                    Maps.<String, Object>newHashMap());
            log.info("exchange declared: {}", exchangeDeclOk);

            final AMQP.Queue.BindOk queueBindOk = channel.queueBind(queueDecl.getQueue(),
                    exchange,
                    "fanout".equals(exchangeType) ? "" : routingKey);
            log.info("queue binding declared: {}", queueBindOk);

            final String consumerTag = channel.basicConsume(queueDecl.getQueue(),
                    true,
                    new FabricMessageConsumer(consumers));
        }
    }

    @Override
    public void send(final MessageController messageController, final AbstractMessage message) {
        final ServiceProto.ContentType contentType = getContentType(messageController, message);
        if (contentType == null) {
            throw new RuntimeException("Could not get content type of message.");
        }
        try {
            final byte[] messageBytes = rawMessageBytes(contentType, message);
            final AMQP.BasicProperties basicProperties = buildBasicProperties(messageController,
                    contentType);
            channel.basicPublish(exchange,
                    "fanout".equals(exchangeType) ? "" : routingKey,
                    basicProperties,
                    messageBytes);
        } catch (final Exception e) {
            // TODO[NKG]: Determine what should happen when a message can't be published.
            log.error("Exception caught publishing message:", e);
        }
    }

    @Override
    public void addConsumer(final TransportConsumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public String getRef() {
        return "rabbitmq://" + exchange + "/" + routingKey;
    }

    private AMQP.BasicProperties buildBasicProperties(
            final MessageController messageController, final ServiceProto.ContentType contentType
    ) {
        final AMQP.BasicProperties.Builder propertiesBuilder = new AMQP.BasicProperties.Builder();
        propertiesBuilder.contentType(JsonFormat.printToString(contentType));

        final ServiceProto.ServiceRef serviceRef = messageController.getDestination();
        propertiesBuilder.appId(JsonFormat.printToString(serviceRef));

        final ServiceProto.ServiceRef senderServiceRef = messageController.getSender();
        propertiesBuilder.replyTo(JsonFormat.printToString(senderServiceRef));

        return propertiesBuilder.build();
    }

    private static class FabricMessageConsumer implements Consumer {

        private static final Logger log = LoggerFactory.getLogger(FabricMessageConsumer.class);

        private final List<TransportConsumer> transportConsumers;

        private FabricMessageConsumer(final List<TransportConsumer> transportConsumers) {
            this.transportConsumers = transportConsumers;
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
        public void handleShutdownSignal(
                final String consumerTag, final ShutdownSignalException sig
        ) {
        }

        @Override
        public void handleRecoverOk(final String consumerTag) {
        }

        @Override
        public void handleDelivery(
                final String consumerTag,
                final Envelope envelope,
                final AMQP.BasicProperties properties,
                final byte[] body
        ) throws IOException {
            final MessageController messageController = buildMessageController(properties);
            for (final TransportConsumer transportConsumer : transportConsumers) {
                try {
                    transportConsumer.handleMessage(messageController, body);
                } catch (final Exception e) {
                    log.error("Error giving message to transport consumer:", e);
                }
            }
        }

        private MessageController buildMessageController(final BasicProperties properties) {
            final String rawContenType = properties.getContentType();
            final Optional<Message> contentType = MessageUtils.fromJson(ServiceProto.ContentType
                    .getDefaultInstance(), rawContenType);

            final String rawDestinationServiceRef = properties.getAppId();
            final Optional<Message> destinationServiceRef = MessageUtils.fromJson(ServiceProto.ServiceRef
                    .getDefaultInstance(), rawDestinationServiceRef);

            final String rawSenderServiceRef = properties.getReplyTo();
            final Optional<Message> senderServiceRef = MessageUtils.fromJson(ServiceProto.ServiceRef
                    .getDefaultInstance(), rawSenderServiceRef);

            return new DefaultMessageController((ServiceProto.ServiceRef) senderServiceRef.get(),
                    (ServiceProto.ServiceRef) destinationServiceRef.get(),
                    (ServiceProto.ContentType) contentType.get());
        }

    }

}
