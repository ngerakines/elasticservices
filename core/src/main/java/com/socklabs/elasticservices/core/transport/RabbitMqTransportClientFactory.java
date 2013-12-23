package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RabbitMqTransportClientFactory implements TransportClientFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqTransportClientFactory.class);

	private final ConnectionFactory connectionFactory;

	public RabbitMqTransportClientFactory(final ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public Optional<TransportClient> get(final Ref ref) {
		LOGGER.debug("Transport client requested for ref {}.", ref.toString());
		if ("rabbitmq".equals(ref.getId())) {
			try {
				final TransportClient transportClient = new RabbitMqTransportClient(
						ref,
						connectionFactory.newConnection());
				return Optional.of(transportClient);
			} catch (IOException e) {
				LOGGER.error("Could not create connection for RabbitMQ transport.", e);
			}
		}
		return Optional.absent();
	}

}
