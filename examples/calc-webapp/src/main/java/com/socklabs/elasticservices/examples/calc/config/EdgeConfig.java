package com.socklabs.elasticservices.examples.calc.config;

import com.google.common.collect.ImmutableList;
import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.DefaultResponseManager;
import com.socklabs.elasticservices.core.message.ResponseManager;
import com.socklabs.elasticservices.core.message.ResponseRemovalWork;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.work.Work;
import com.socklabs.elasticservices.examples.calc.CalcEdgeService;
import com.socklabs.elasticservices.examples.calc.service.CalcMessageFactory;
import com.socklabs.elasticservices.http.HttpTransportController;
import com.socklabs.elasticservices.rabbitmq.RabbitMqTransport;
import org.joda.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Configuration
public class EdgeConfig {

	@Resource
	private Environment environment;

	@Resource
	private ServiceProto.ComponentRef localComponentRef;

	@Resource
	private ServiceRegistry serviceRegistry;

	@Resource
	private ConnectionFactory connectionFactory;

	@Bean
	public ServiceProto.ServiceRef calcEdgeServiceRef() {
		return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("calcEdge").build();
	}

	@Bean
	public Ref calcEdgeServiceTransportRef() {
		return RefUtils.rabbitMqTransportRef(
				environment.getRequiredProperty("service.calcEdge.exchange"),
				environment.getRequiredProperty("service.calcEdge.routing_key"),
				"direct");
	}

	@Bean
	public Transport calcEdgeTransport() {
		try {
			return new RabbitMqTransport(connectionFactory.newConnection(), calcEdgeServiceTransportRef());
		} catch (final IOException e) {
			throw new RuntimeException("Could not create AMQP transport for calc service.", e);
		}
	}

	@Bean
	public Service calcEdgeService() {
		return new CalcEdgeService(
				calcEdgeServiceRef(),
				calcResponseManager(),
				ImmutableList.<MessageFactory>of(new CalcMessageFactory()));
	}

	@Bean(name = "calcResponseManager")
	public ResponseManager calcResponseManager() {
		return new DefaultResponseManager(calcEdgeServiceRef(), serviceRegistry);
	}

	@Bean
	public Work staleEdgeFutureRemoverWork() {
		return new ResponseRemovalWork(
				"service:calc:edge:cleanup",
				calcResponseManager(),
				60,
				Duration.standardMinutes(5));
	}

	@Bean
	public Ref httpControllerRef() {
		return RefUtils.httpTransportRef(
				"localhost",
				environment.getRequiredProperty("transport.http.port", Integer.class),
				"calcEdge");
	}

	@Bean
	public HttpTransportController httpTransportController() {
		return new HttpTransportController(serviceRegistry, httpControllerRef());
	}

	@PostConstruct
	public void registerService() {
		serviceRegistry.registerService(calcEdgeService(), calcEdgeTransport(), httpTransportController());
	}

}
