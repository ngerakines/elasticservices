package com.socklabs.elasticservices.core.config;

import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.service.DefaultServiceRegistry;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.DelegatingTransportClientFactory;
import com.socklabs.elasticservices.core.transport.LocalTransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class ServiceConfig {

	@Resource
	private Environment environment;

	@Bean
	public ServiceRegistry serviceRegistry() {
		return new DefaultServiceRegistry(localComponentRef(), transportClientFactory());
	}

	@Bean(name = { "transportClientFactory", "delegatingTransportClientFactory" })
	public TransportClientFactory transportClientFactory() {
		final DelegatingTransportClientFactory delegatingTransportClientFactory =
				new DelegatingTransportClientFactory();
		delegatingTransportClientFactory.addDelegate(localTransportClientFactory());
		return delegatingTransportClientFactory;
	}

	@Bean(name = "localTransportClientFactory")
	public TransportClientFactory localTransportClientFactory() {
		return new LocalTransportClientFactory();
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
