package com.socklabs.elasticservices.gossip;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.config.WorkConfig;
import com.socklabs.elasticservices.core.message.DefaultMessageFactory;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.DefaultServiceRegistry;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServicePresenceListener;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.LocalTransportClientFactory;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import com.socklabs.elasticservices.core.work.Work;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by nick.gerakines on 1/9/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class GossipTest {

	@Configuration
	@Import({ServiceConfig.class, WorkConfig.class})
	@PropertySource({ "classpath:com/socklabs/elasticservices/gossip/test.properties" })
	static class ContextConfiguration {

		@Resource
		private ServiceProto.ComponentRef localComponentRef;

		@Resource
		private ServiceRegistry serviceRegistry;

		@Resource(name = "localTransportClientFactory")
		private TransportClientFactory localTransportClientFactory;

		@Bean
		public MessageFactory gossipMessageFactory() {
			return new DefaultMessageFactory(
					ImmutableList.<Message>of(
							GossipServiceProto.ComponentOnline.getDefaultInstance(),
							GossipServiceProto.ComponentStatus.getDefaultInstance()));
		}

		@Bean
		public ServiceProto.ServiceRef gossipServiceRef() {
			return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("gossip").build();
		}

		@Bean
		public TestPresenceListener testPresenceListener() {
			return new TestPresenceListener();
		}

		@Bean
		public Service gossipService() {
			final List<ServicePresenceListener> servicePresenceListeners = Lists.newArrayList();
			// NKG: This should be cleaned up in the future.
			if (serviceRegistry instanceof DefaultServiceRegistry) {
				servicePresenceListeners.add((ServicePresenceListener) serviceRegistry);
			}
			servicePresenceListeners.add(testPresenceListener());
			return new GossipService(gossipMessageFactory(), gossipServiceRef(), servicePresenceListeners);
		}

		@Bean
		public Work broadcastWork() {
			return new BroadcastWork(gossipServiceRef(), serviceRegistry);
		}

		// ----

		@Bean
		public Ref mockGossipTransportRef() {
			return RefUtils.localTransportRef("gossip");
		}

		@Bean
		public Transport mockGossipTransport() {
			final Transport transport = mock(Transport.class);
			when(transport.getRef()).thenReturn(mockGossipTransportRef());
			return transport;
		}

		@PostConstruct
		public void registerService() {
			serviceRegistry.registerService(gossipService(), mockGossipTransport());
			if (localTransportClientFactory instanceof LocalTransportClientFactory) {
				final LocalTransportClientFactory localTransportClientFactoryImpl =
						(LocalTransportClientFactory) localTransportClientFactory;
				localTransportClientFactoryImpl.addService("gossip", gossipService());
			}
			serviceRegistry.initTransportClient(gossipServiceRef(), mockGossipTransportRef());
		}

	}

	@Resource
	private Service gossipService;

	@Resource
	private TestPresenceListener testPresenceListener;

	@Test
	public void load() {
		Assert.assertNotNull(gossipService);
		Assert.assertTrue(
				waitUntil(
						new Callable<Integer>() {
							@Override public Integer call() throws Exception { return testPresenceListener.getCount(); }
						},
						3,
						60));
		Assert.assertTrue(testPresenceListener.getCount() > 0);
	}

	private <T> boolean waitUntil(final Callable<T> callable, final T value, final int seconds) {
		final DateTime start = DateTime.now();
		while (DateTime.now().isBefore(start.plusSeconds(seconds))) {
			try {
				if (callable.call().equals(value)) {
					return true;
				}
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			} catch (final Exception e) {
				return false;
			}
		}
		return false;
	}

	private static class TestPresenceListener implements ServicePresenceListener {

		private static final Logger LOGGER = LoggerFactory.getLogger(TestPresenceListener.class);

		final private AtomicInteger counter = new AtomicInteger(0);

		@Override
		public void updateComponentServices(
				final ServiceProto.ComponentRef componentRef,
				final Multimap<ServiceProto.ServiceRef, String> services,
				final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags) {
			LOGGER.info("counter now at {}.", counter.incrementAndGet());
		}

		public int getCount() {
			return counter.get();
		}

	}

}
