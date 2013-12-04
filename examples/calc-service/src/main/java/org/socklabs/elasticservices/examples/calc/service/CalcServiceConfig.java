package org.socklabs.elasticservices.examples.calc.service;


import com.rabbitmq.client.ConnectionFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.service.Service;
import org.socklabs.elasticservices.core.service.ServiceRegistry;
import org.socklabs.elasticservices.core.transport.RabbitMqTransport;
import org.socklabs.elasticservices.core.transport.Transport;
import org.socklabs.elasticservices.core.transport.TransportFactory;
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
	private TransportFactory transportFactory;

	@Resource
	private ConnectionFactory connectionFactory;

	@Bean
	public ServiceProto.ServiceRef calcServiceRef() {
		return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("calc").build();
	}

	@Bean
	public Transport calcTransport() {
		final String exchange = environment.getRequiredProperty("service.calc.exchange");
		final String routingKey = environment.getRequiredProperty("service.calc.routing_key");
		try {
			return new RabbitMqTransport(
					connectionFactory.newConnection(), exchange, routingKey, "direct", true);
		} catch (final IOException e) {
			throw new RuntimeException("Could not create AMQP transport for calc service.", e);
		}
	}

	@Bean
	public Service calcService() {
		return new CalcService(calcServiceRef(), serviceRegistry);
	}

	@PostConstruct
	public void registerService() {
		serviceRegistry.registerService(calcService());
		serviceRegistry.bindTransportToService(calcServiceRef(), calcTransport());
	}

}
