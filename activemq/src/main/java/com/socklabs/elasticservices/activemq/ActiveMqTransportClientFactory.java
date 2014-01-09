package com.socklabs.elasticservices.activemq;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.transport.TransportClient;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;

/**
 * Created by nick.gerakines on 1/9/14.
 */
public class ActiveMqTransportClientFactory implements TransportClientFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqTransportClientFactory.class);

	private final ConnectionFactory connectionFactory;

	public ActiveMqTransportClientFactory(final ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public Optional<TransportClient> get(Ref ref) {
		LOGGER.debug("Transport client requested for ref {}.", ref.toString());
		if ("activemq".equals(ref.getId())) {
			try {
				final ActiveMqTransportClient transportClient = new ActiveMqTransportClient(ref, connectionFactory);
				transportClient.init();
				return Optional.<TransportClient>of(transportClient);
			} catch (final Exception e) {
				LOGGER.error("Could not create connection for ActiveMq transport.", e);
			}
		}
		return Optional.absent();
	}

}
