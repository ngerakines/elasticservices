package com.socklabs.elasticservices.examples.calc.service;


import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.DefaultMessageFactory;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.examples.calc.CalcServiceProto;
import com.socklabs.elasticservices.rabbitmq.RabbitMqTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Configuration
public class CalcServiceConfig {

	@Resource
	private Environment environment;

	@Resource
	private ServiceProto.ComponentRef localComponentRef;

	@Resource
	private ServiceRegistry serviceRegistry;

	@Resource
	private ConnectionFactory connectionFactory;

	@Bean
	public MessageFactory calcMessageFactory() {
		return new DefaultMessageFactory(
				ImmutableList.<Message>of(
						CalcServiceProto.Add.getDefaultInstance(),
						CalcServiceProto.Subtract.getDefaultInstance(),
						CalcServiceProto.Result.getDefaultInstance()));
	}

	@Bean
	public ServiceProto.ServiceRef calcServiceRef() {
		return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("calc").build();
	}

	@Bean
	public Ref calcServiceTransportRef() {
		return RefUtils.rabbitMqTransportRef(
				environment.getRequiredProperty("service.calc.exchange"),
				environment.getRequiredProperty("service.calc.routing_key"),
				"direct");
	}

	@Bean
	public Transport calcTransport() {
		try {
			return new RabbitMqTransport(
					connectionFactory.newConnection(),
					calcServiceTransportRef());
		} catch (final IOException e) {
			throw new RuntimeException("Could not create AMQP transport for calc service.", e);
		}
	}

	@Bean
	public Service calcService() {
		return new CalcService(ImmutableList.of(calcMessageFactory()), calcServiceRef(), serviceRegistry);
	}

	@PostConstruct
	public void registerService() {
		serviceRegistry.registerService(calcService(), calcTransport());
	}

}
