package com.socklabs.elasticservices.core.transport;

import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.misc.Ref;

import java.io.IOException;

public class DefaultTransportFactory implements TransportFactory {

	private final ConnectionFactory connectionFactory;


	public DefaultTransportFactory(final ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public Transport get(final Ref transportRef) {
		if ("rabbitmq".equals(transportRef.getId())) {
			try {
				return new RabbitMqTransport(
						connectionFactory.newConnection(),
						transportRef);
			} catch (final IOException e) {
				throw new RuntimeException("Could not create AMQP transport.", e);
			}
		}
		return null;
	}

}

