package org.socklabs.elasticservices.examples.calc.config;

import com.google.common.collect.ImmutableList;
import com.rabbitmq.client.ConnectionFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.edge.DefaultEdgeManager;
import org.socklabs.elasticservices.core.edge.EdgeManager;
import org.socklabs.elasticservices.core.message.MessageFactory;
import org.socklabs.elasticservices.core.service.Service;
import org.socklabs.elasticservices.core.service.ServiceRegistry;
import org.socklabs.elasticservices.core.transport.RabbitMqTransport;
import org.socklabs.elasticservices.core.transport.Transport;
import org.socklabs.elasticservices.core.transport.TransportFactory;
import org.socklabs.elasticservices.examples.calc.CalcEdgeService;
import org.socklabs.elasticservices.examples.calc.service.CalcMessageFactory;
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
    private TransportFactory transportFactory;

    @Resource
    private ConnectionFactory connectionFactory;

    @Bean
    public ServiceProto.ServiceRef calcEdgeServiceRef() {
        return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("calcEdge").build();
    }

    @Bean
    public Transport calcEdgeTransport() {
        final String exchange = environment.getRequiredProperty("service.calcEdge.exchange");
        final String routingKey = environment.getRequiredProperty("service.calcEdge.routing_key");
        try {
            return new RabbitMqTransport(
                    connectionFactory.newConnection(),
                    exchange,
                    routingKey,
                    "direct",
                    true);
        }
        catch (final IOException e) {
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

    @PostConstruct
    public void registerService() {
        serviceRegistry.registerService(calcEdgeService());
        serviceRegistry.bindTransportToService(calcEdgeServiceRef(), calcEdgeTransport());
    }

}
