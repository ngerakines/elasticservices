package com.socklabs.elasticservices.gossip;

import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.RabbitMqTransport;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.work.Work;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Configuration
public class GossipServiceConfig {

	@Resource
	private Environment environment;

	@Resource
	private ServiceProto.ComponentRef localComponentRef;

	@Resource
	private ServiceRegistry serviceRegistry;

	@Resource
	private ConnectionFactory connectionFactory;

	@Bean
	public ServiceProto.ServiceRef gossipServiceRef() {
		return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("gossip").build();
	}

	@Bean
	public Transport gossipTransport() {
		final String exchange = environment.getRequiredProperty("service.gossip.exchange");
		final String routingKey = environment.getRequiredProperty("service.gossip.routing_key");
		try {
			return new RabbitMqTransport(
					connectionFactory.newConnection(), exchange, routingKey, "fanout", true);
		} catch (final IOException e) {
			throw new RuntimeException("Could not create AMQP transport for gossip service.", e);
		}
	}

	@Bean
	public Service gossipService() {
		return new GossipService(gossipServiceRef(), serviceRegistry);
	}

	@Bean
	public Work broadcastWork() {
		return new BroadcastWork(gossipServiceRef(), serviceRegistry);
	}

	@PostConstruct
	public void registerService() {
		serviceRegistry.registerService(gossipService());
		serviceRegistry.bindTransportToService(gossipServiceRef(), gossipTransport());
	}

}
