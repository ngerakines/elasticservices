package com.socklabs.elasticservices.core.config;

import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.service.DefaultServiceRegistry;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.DefaultTransportClientFactory;
import com.socklabs.elasticservices.core.transport.DefaultTransportFactory;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class ServiceConfig {

	@Resource
	private Environment environment;
	@Resource
	private ConnectionFactory rabbitMqConnectionFactory;

	@Bean
	public ServiceRegistry serviceRegistry() {
		return new DefaultServiceRegistry(localComponentRef(), transportClientFactory());
	}

	@Bean
	public TransportFactory transportFactory() {
		return new DefaultTransportFactory(rabbitMqConnectionFactory);
	}

	@Bean
	public TransportClientFactory transportClientFactory() {
		return new DefaultTransportClientFactory(rabbitMqConnectionFactory);
	}

	@Bean
	public ServiceProto.ComponentRef localComponentRef() {
		return ServiceProto.ComponentRef
				.newBuilder()
				.setSite(environment.getRequiredProperty("component.site"))
				.setCluster(environment.getRequiredProperty("component.cluster"))
				.setComponentId(environment.getRequiredProperty("component.id"))
				.build();
	}

}
