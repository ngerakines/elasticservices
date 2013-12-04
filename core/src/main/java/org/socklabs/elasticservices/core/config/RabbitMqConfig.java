package org.socklabs.elasticservices.core.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class RabbitMqConfig {

	@Resource
	private Environment environment;

	@Bean
	public ConnectionFactory rabbitMqConnectionFactory() {
		final ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(environment.getRequiredProperty("rabbitmq.host"));
		connectionFactory.setPort(environment.getRequiredProperty("rabbitmq.port", Integer.class));
		connectionFactory.setUsername(environment.getRequiredProperty("rabbitmq.user"));
		connectionFactory.setPassword(environment.getRequiredProperty("rabbitmq.pass"));
		return connectionFactory;
	}

}
