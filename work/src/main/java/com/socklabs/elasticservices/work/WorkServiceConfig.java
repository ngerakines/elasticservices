package com.socklabs.elasticservices.work;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.message.DefaultMessageFactory;
import com.socklabs.elasticservices.core.message.MessageFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.rabbitmq.RabbitMqTransport;

/**
 * Created by ngerakines on 12/16/13.
 */
@Configuration
public class WorkServiceConfig {

	@Resource
	private Environment environment;

	@Resource
	private ServiceProto.ComponentRef localComponentRef;

	@Resource
	private ServiceRegistry serviceRegistry;

	@Resource
	private ConnectionFactory connectionFactory;

	@Bean
	public MessageFactory workMessageFactory() {
		return new DefaultMessageFactory(
				ImmutableList.<Message>of(
						WorkServiceProto.ListRequest.getDefaultInstance(),
						WorkServiceProto.ListResponse.getDefaultInstance()));
	}

	@Bean
	public ServiceProto.ServiceRef workServiceRef() {
		return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("work").build();
	}

	@Bean
	public Ref workTransportRef() {
		return RefUtils.rabbitMqTransportRef(
				environment.getRequiredProperty("service.work.exchange"),
				environment.getRequiredProperty("service.work.routing_key"),
				"direct");
	}

	@Bean
	public Transport workTransport() {
		try {
			return new RabbitMqTransport(connectionFactory.newConnection(), workTransportRef());
		} catch (final IOException e) {
			throw new RuntimeException("Could not create AMQP transport for work service.", e);
		}
	}

	@Bean
	public Service workService() {
		return new WorkService(ImmutableList.of(workMessageFactory()), workServiceRef(), serviceRegistry);
	}

	@PostConstruct
	public void registerService() {
		serviceRegistry.registerService(workService(), workTransport());
		serviceRegistry.initTransportClient(workServiceRef(), workTransportRef());
	}

}
