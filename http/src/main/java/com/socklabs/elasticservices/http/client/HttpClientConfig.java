package com.socklabs.elasticservices.http.client;

import com.socklabs.elasticservices.core.transport.DelegatingTransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by ngerakines on 12/28/13.
 */
@Configuration
public class HttpClientConfig {

	@Resource(name = "delegatingTransportClientFactory")
	private TransportClientFactory transportClientFactory;

	@Bean
	public TransportClientFactory httpTransportClientFactory() {
		return new HttpTransportClientFactory();
	}

	@PostConstruct
	public void setTransportClientFactoryDelegates() {
		if (transportClientFactory instanceof DelegatingTransportClientFactory) {
			((DelegatingTransportClientFactory) transportClientFactory).addDelegate(httpTransportClientFactory());
		}
	}

}
