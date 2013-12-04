package org.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;

public class DefaultTransportFactory implements TransportFactory {

	private final Optional<ConnectionFactory> connectionFactory;

	public DefaultTransportFactory() {
		this.connectionFactory = Optional.absent();
	}

	public DefaultTransportFactory(final ConnectionFactory connectionFactory) {
		this.connectionFactory = Optional.of(connectionFactory);
	}

	@Override
	public Transport get(final String transportRef) {
		if (transportRef.startsWith("local://")) {
			final String id = transportRef.substring("local://".length());
			return new LocalTransport(id);
		} else if (transportRef.startsWith("rabbitmq://") && connectionFactory.isPresent()) {
			final Splitter splitter = Splitter.on("/").trimResults().omitEmptyStrings();
			final List<String> parts = ImmutableList.copyOf(splitter.split(transportRef));
			Preconditions.checkArgument(parts.size() == 3); // "rabbitmq:", exchange, routing key

			final String exchange = parts.get(1);
			final String routingKey = parts.get(2);

			try {
				return new RabbitMqTransport(
						connectionFactory.get().newConnection(), exchange, routingKey, "direct", false);
			} catch (final IOException e) {
				throw new RuntimeException("Could not create AMQP transport.", e);
			}
		}
		return null;
	}

}

