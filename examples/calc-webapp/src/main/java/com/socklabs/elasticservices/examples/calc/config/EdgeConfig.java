package com.socklabs.elasticservices.examples.calc.config;

import com.google.common.collect.ImmutableList;
import com.rabbitmq.client.ConnectionFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import org.joda.time.Duration;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.edge.DefaultEdgeManager;
import com.socklabs.elasticservices.core.edge.EdgeManager;
import com.socklabs.elasticservices.core.edge.StaleEdgeFutureRemoverWork;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.RabbitMqTransport;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.work.Work;
import com.socklabs.elasticservices.examples.calc.CalcEdgeService;
import com.socklabs.elasticservices.examples.calc.service.CalcMessageFactory;
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
				edgeManager(),
				ImmutableList.<MessageFactory>of(new CalcMessageFactory()));
	}

	@Bean(name = "calcEdgeManager")
	public EdgeManager edgeManager() {
		return new DefaultEdgeManager(calcEdgeServiceRef(), serviceRegistry);
	}

	@Bean
	public Work staleEdgeFutureRemoverWork() {
		return new StaleEdgeFutureRemoverWork(
				"service:calc:edge:cleanup",
				edgeManager(),
				60,
				Duration.standardMinutes(5));
	}

	@PostConstruct
	public void registerService() {
		serviceRegistry.registerService(calcEdgeService(), calcEdgeTransport());
	}

}
