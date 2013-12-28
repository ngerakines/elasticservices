package com.socklabs.elasticservices.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.transport.DelegatingTransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by ngerakines on 12/28/13.
 */
@Configuration
public class RabbitMqConfig {

	@Resource
	private Environment environment;

	@Resource(name = "delegatingTransportClientFactory")
	private TransportClientFactory transportClientFactory;

	@Bean
	public ConnectionFactory rabbitMqConnectionFactory() {
		final ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(environment.getRequiredProperty("rabbitmq.host"));
		connectionFactory.setPort(environment.getRequiredProperty("rabbitmq.port", Integer.class));
		connectionFactory.setUsername(environment.getRequiredProperty("rabbitmq.user"));
		connectionFactory.setPassword(environment.getRequiredProperty("rabbitmq.pass"));
		return connectionFactory;
	}

	@Bean
	public TransportClientFactory rabbitMqTransportClientFactory() {
		return new RabbitMqTransportClientFactory(rabbitMqConnectionFactory());
	}

	@PostConstruct
	public void setTransportClientFactoryDelegates() {
		if (transportClientFactory instanceof DelegatingTransportClientFactory) {
			((DelegatingTransportClientFactory) transportClientFactory).addDelegate(rabbitMqTransportClientFactory());
		}
	}
}
