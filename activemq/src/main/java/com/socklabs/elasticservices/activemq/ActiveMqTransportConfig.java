package com.socklabs.elasticservices.activemq;

import com.socklabs.elasticservices.core.transport.DelegatingTransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;

/**
 * Created by nick.gerakines on 1/9/14.
 */
@Configuration
public class ActiveMqTransportConfig {

	@Resource
	private Environment environment;

	@Resource(name = "delegatingTransportClientFactory")
	private TransportClientFactory transportClientFactory;

	@Resource
	private ConnectionFactory connectionFactory;

	@Bean
	public TransportClientFactory activeMqTransportClientFactory() {
		return new ActiveMqTransportClientFactory(connectionFactory);
	}

	@PostConstruct
	public void setTransportClientFactoryDelegates() {
		if (transportClientFactory instanceof DelegatingTransportClientFactory) {
			((DelegatingTransportClientFactory) transportClientFactory).addDelegate(activeMqTransportClientFactory());
		}
	}

}
