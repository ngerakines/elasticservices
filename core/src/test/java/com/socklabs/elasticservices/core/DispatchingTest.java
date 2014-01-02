package com.socklabs.elasticservices.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.socklabs.elasticservices.core.config.PropertiesConfig;
import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.DefaultMessageFactory;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.DefaultMessageController;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
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
import java.util.Map;

/**
 * Created by ngerakines on 1/2/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class DispatchingTest {

	@Configuration
	@Import({ PropertiesConfig.class, ServiceConfig.class })
	@PropertySource({ "classpath:com/socklabs/elasticservices/core/test.properties" })
	static class ContextConfiguration {

		@Resource
		private ServiceProto.ComponentRef localComponentRef;

		@Resource
		private ServiceRegistry serviceRegistry;

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
		public CapturingService capturingService() {
			return new CapturingService(capturingServiceRef(), Lists.newArrayList(messageFactory()));
		}

		@Bean
		public ServiceProto.ServiceRef capturingServiceRef() {
			return ServiceProto.ServiceRef
					.newBuilder()
					.setComponentRef(localComponentRef)
					.setServiceId("capture")
					.build();
		}

		@Bean
		public Ref capturingTransportRef() {
			final Ref.Builder refBuilder = Ref.builder("debug");
			refBuilder.addValue("service", "capture");
			refBuilder.addValue("order", "25000");
			return refBuilder.build();
		}

		@Bean
		public DebugTransport debugTransport() {
			return new DebugTransport(capturingTransportRef());
		}

		@PostConstruct
		public void registerService() {
			serviceRegistry.registerService(capturingService(), debugTransport());
		}
	}

	@Resource
	private ServiceProto.ComponentRef localComponent;

	@Resource
	private CapturingService capturingService;

	@Resource
	private DebugTransport debugTransport;

	@Test
	public void messageReceived() {
		final MessageController messageController =
				new DefaultMessageController(
						capturingService.getServiceRef(),
						capturingService.getServiceRef(),
						ContentTypes.fromJsonClass(ServiceProto.ComponentRef.class));
		debugTransport.dispatch(messageController, rawMessageBytes(messageController.getContentType(), localComponent));
		Assert.assertEquals(capturingService.getMessageCount(), 1);
	}

	private <M extends com.google.protobuf.Message> byte[] rawMessageBytes(
			final ServiceProto.ContentType contentType,
			final M message) {
		if (contentType.getValue().equals(ContentTypes.CONTENT_TYPE_JSON)) {
			return JsonFormat.printToString(message).getBytes();
		}
		return message.toByteArray();
	}
}
