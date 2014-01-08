package com.socklabs.elasticservices.gossip;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.DefaultMessageFactory;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.DefaultServiceRegistry;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServicePresenceListener;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.work.Work;
import com.socklabs.elasticservices.rabbitmq.RabbitMqTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

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
	public MessageFactory gossipMessageFactory() {
		return new DefaultMessageFactory(
				ImmutableList.<Message>of(
						GossipServiceProto.ComponentOnline.getDefaultInstance(),
						GossipServiceProto.ComponentStatus.getDefaultInstance()));
	}

	@Bean
	public ServiceProto.ServiceRef gossipServiceRef() {
		return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("gossip").build();
	}

	@Bean
	public Ref gossipTransportRef() {
		return RefUtils.rabbitMqTransportRef(
				environment.getRequiredProperty("service.gossip.exchange"),
				environment.getRequiredProperty("service.gossip.routing_key"),
				"fanout");
	}

	@Bean
	public Transport gossipTransport() {
		try {
			return new RabbitMqTransport(connectionFactory.newConnection(), gossipTransportRef());
		} catch (final IOException e) {
			throw new RuntimeException("Could not create AMQP transport for gossip service.", e);
		}
	}

	@Bean
	public Service gossipService() {
		final List<ServicePresenceListener> servicePresenceListeners = Lists.newArrayList();
		// NKG: This should be cleaned up in the future.
		if (serviceRegistry instanceof DefaultServiceRegistry) {
			servicePresenceListeners.add((ServicePresenceListener) serviceRegistry);
		}
		return new GossipService(gossipMessageFactory(), gossipServiceRef(), servicePresenceListeners);
	}

	@Bean
	public Work broadcastWork() {
		return new BroadcastWork(gossipServiceRef(), serviceRegistry);
	}

	@PostConstruct
	public void registerService() {
		serviceRegistry.registerService(gossipService(), gossipTransport());
		serviceRegistry.initTransportClient(gossipServiceRef(), gossipTransportRef());
	}

}
