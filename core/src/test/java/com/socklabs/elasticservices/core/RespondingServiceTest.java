package com.socklabs.elasticservices.core;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.config.PropertiesConfig;
import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.message.DefaultMessageFactory;
import com.socklabs.elasticservices.core.message.DefaultResponseManager;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.message.ResponseManager;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.ResponseService;
import com.socklabs.elasticservices.core.service.Service;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.transport.LocalTransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import junit.framework.Assert;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ngerakines on 2/26/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class RespondingServiceTest {

	@Resource
	private ServiceProto.ServiceRef phoneServiceRef;

	@Resource
	private ResponseManager responseManager;

	@Test
	public void ok() {
		final PhoneRespondingService service = PhoneRespondingServiceStub.create(phoneServiceRef, responseManager);
		final ListenableFuture<TestProto.Bar> future =
				service.call(TestProto.Foo.newBuilder().setCurrentTime(System.currentTimeMillis()).build());
		try {
			Assert.assertEquals(1, future.get().getBaziCount());
		} catch (ExecutionException | InterruptedException e) {
			Assert.fail(e.getMessage());
		}
	}

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

		@PostConstruct
		public void registerService() {
			if (localTransportClientFactory instanceof LocalTransportClientFactory) {
				final LocalTransportClientFactory localTransportClientFactoryImpl =
						(LocalTransportClientFactory) localTransportClientFactory;
				localTransportClientFactoryImpl.addService("phone", phoneService());
				localTransportClientFactoryImpl.addService("response", responseService());
			}
			serviceRegistry.registerService(phoneService());
			serviceRegistry.initTransportClient(phoneServiceRef(), phoneLocalTransportRef());
			serviceRegistry.registerService(responseService());
			serviceRegistry.initTransportClient(responseServiceRef(), responseLocalTransportRef());
		}

		@Bean
		public ServiceProto.ServiceRef responseServiceRef() {
			return ServiceProto.ServiceRef
					.newBuilder()
					.setComponentRef(localComponentRef)
					.setServiceId("response")
					.build();
		}

		@Bean
		public Service responseService() {
			return new ResponseService(responseServiceRef(), ImmutableList.of(messageFactory()), responseManager());
		}

		@Bean
		public ResponseManager responseManager() {
			return new DefaultResponseManager(responseServiceRef(), serviceRegistry);
		}

		@Bean
		public Ref responseLocalTransportRef() {
			return RefUtils.localTransportRef("response");
		}

		@Bean
		public ServiceProto.ServiceRef phoneServiceRef() {
			return ServiceProto.ServiceRef
					.newBuilder()
					.setComponentRef(localComponentRef)
					.setServiceId("phone")
					.build();
		}

		@Bean
		public Ref phoneLocalTransportRef() {
			return RefUtils.localTransportRef("phone");
		}

		@Bean
		public Service phoneService() {
			return new DefaultPhoneRespondingService(
					phoneServiceRef(),
					ImmutableList.of(messageFactory()),
					serviceRegistry);
		}

		@Bean
		public MessageFactory messageFactory() {
			final List<Message> messages =
					ImmutableList.<Message> of(
							TestProto.Foo.getDefaultInstance(),
							TestProto.Bar.getDefaultInstance(),
							ServiceProto.EncodedError.getDefaultInstance());
			return new DefaultMessageFactory(messages);
		}

	}

}
