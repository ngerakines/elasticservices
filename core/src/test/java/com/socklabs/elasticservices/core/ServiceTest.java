package com.socklabs.elasticservices.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.config.PropertiesConfig;
import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.DefaultMessageFactory;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.LocalTransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by ngerakines on 1/2/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ServiceTest {

	@Configuration
	@Import({ PropertiesConfig.class, ServiceConfig.class })
	@PropertySource({ "classpath:com/socklabs/elasticservices/core/test.properties" })
	static class ContextConfiguration {

		@Resource
		private ServiceProto.ComponentRef localComponentRef;

		@Resource
		private ServiceRegistry serviceRegistry;

		@Resource(name = "localTransportClientFactory")
		private TransportClientFactory localTransportClientFactory;

		@Bean
		public MessageFactory messageFactory() {
			final Map<String, Message> stuff =
					ImmutableMap.<String, Message> of(
							ServiceProto.ComponentRef.class.getName(),
							ServiceProto.ComponentRef.getDefaultInstance(),
							ServiceProto.ServiceRef.class.getName(),
							ServiceProto.ServiceRef.getDefaultInstance());
			return new DefaultMessageFactory(stuff);
		}

		@Bean
		public Service mockService() {
			final Service service = mock(Service.class);
			when(service.getMessageFactories()).thenReturn(Lists.newArrayList(messageFactory()));
			when(service.getServiceRef()).thenReturn(mockServiceRef());
			return service;
		}

		@Bean
		public ServiceProto.ServiceRef mockServiceRef() {
			return ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("mock").build();
		}

		@Bean
		public Ref mockTransportRef() {
			return RefUtils.localTransportRef("mock");
		}

		@PostConstruct
		public void registerService() {
			serviceRegistry.registerService(mockService());
			if (localTransportClientFactory instanceof LocalTransportClientFactory) {
				final LocalTransportClientFactory localTransportClientFactoryImpl =
						(LocalTransportClientFactory) localTransportClientFactory;
				localTransportClientFactoryImpl.addService("mock", mockService());
			}
			serviceRegistry.initTransportClient(mockServiceRef(), mockTransportRef());
		}
	}

	@Resource
	private ServiceProto.ComponentRef localComponentRef;

	@Resource
	private ServiceRegistry serviceRegistry;

	@Resource
	private ServiceProto.ServiceRef mockServiceRef;

	@Resource
	private Service mockService;

	@After
	public void after() {
		reset(mockService);
	}

	@Test
	public void verifyLocalComponent() {
		Assert.assertEquals(localComponentRef.getSite(), "local");
		Assert.assertEquals(localComponentRef.getCluster(), "test");
		Assert.assertEquals(localComponentRef.getComponentId(), "test1");
	}

	@Test
	public void handleMessage() {
		final ServiceProto.ComponentRef request = ServiceProto.ComponentRef.newBuilder().build();
		serviceRegistry.sendMessage(
				mockServiceRef,
				mockServiceRef,
				request,
				ContentTypes.fromJsonClass(ServiceProto.ComponentRef.class));
		verify(mockService, times(1)).handleMessage(any(MessageController.class), any(Message.class));
		verifyNoMoreInteractions(mockService);
	}

	@Test
	public void invalidDestination() {
		final ServiceProto.ComponentRef request = ServiceProto.ComponentRef.newBuilder().build();
		serviceRegistry.sendMessage(
				ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponentRef).setServiceId("fake").build(),
				mockServiceRef,
				request,
				ContentTypes.fromJsonClass(ServiceProto.ComponentRef.class));
		verifyNoMoreInteractions(mockService);
	}

	@Test
	public void cachedTransportClient() {
		final ServiceProto.ComponentRef request = ServiceProto.ComponentRef.newBuilder().build();
		serviceRegistry.sendMessage(
				mockServiceRef,
				mockServiceRef,
				request,
				ContentTypes.fromJsonClass(ServiceProto.ComponentRef.class));
		serviceRegistry.sendMessage(
				mockServiceRef,
				mockServiceRef,
				request,
				ContentTypes.fromJsonClass(ServiceProto.ComponentRef.class));
		verify(mockService, times(2)).handleMessage(any(MessageController.class), any(Message.class));
		verifyNoMoreInteractions(mockService);
	}

	@Test(expected = RuntimeException.class)
	public void doubleRegister() {
		when(mockService.getServiceRef()).thenReturn(mockServiceRef);
		serviceRegistry.registerService(mockService);
	}

}
