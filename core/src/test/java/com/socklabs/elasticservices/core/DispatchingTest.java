package com.socklabs.elasticservices.core;

import com.google.common.base.Optional;
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
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
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
							TestProto.Foo.class.getName(),
							TestProto.Foo.getDefaultInstance(),
							TestProto.Bar.class.getName(),
							TestProto.Bar.getDefaultInstance(),
							TestProto.Baz.class.getName(),
							TestProto.Baz.getDefaultInstance());
			return new DefaultMessageFactory(stuff);
		}

		@Bean
		public CapturingService capturingService() {
			return new CapturingService(capturingServiceRef(), Lists.newArrayList(messageFactory()));
		}

		@Bean
		public ExplodingService explodingService() {
			return new ExplodingService(explodingServiceRef(), Lists.newArrayList(messageFactory()));
		}

		@Bean
		public ServiceProto.ServiceRef explodingServiceRef() {
			return ServiceProto.ServiceRef
					.newBuilder()
					.setComponentRef(localComponentRef)
					.setServiceId("explode")
					.build();
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
		public Ref explodingTransportRef() {
			final Ref.Builder refBuilder = Ref.builder("debug");
			refBuilder.addValue("service", "capture");
			refBuilder.addValue("order", "25000");
			return refBuilder.build();
		}

		@Bean
		public DebugTransport debugTransport() {
			return new DebugTransport(capturingTransportRef());
		}

		@Bean
		public DebugTransport explodingdebugTransport() {
			return new DebugTransport(explodingTransportRef());
		}

		@PostConstruct
		public void registerService() {
			serviceRegistry.registerService(capturingService(), debugTransport());
			serviceRegistry.registerService(explodingService(), explodingdebugTransport());
		}
	}

	@Resource
	private ServiceProto.ComponentRef localComponent;

	@Resource
	private CapturingService capturingService;

	@Resource
	private DebugTransport debugTransport;

	@Resource
	private ServiceProto.ServiceRef explodingServiceRef;

	@Resource
	private DebugTransport explodingdebugTransport;

	@Test
	@DirtiesContext
	public void messageReceived() {
		final MessageController messageController =
				new DefaultMessageController(
						capturingService.getServiceRef(),
						capturingService.getServiceRef(),
						ContentTypes.fromClass(TestProto.Foo.class));
		debugTransport.dispatch(
				messageController,
				rawMessageBytes(
						messageController.getContentType(),
						TestProto.Foo.newBuilder().setCurrentTime(System.currentTimeMillis()).build()));
		Assert.assertEquals(capturingService.getMessageCount(), 1);
	}

	@Test
	@DirtiesContext
	public void invalidDestination() {
		final ServiceProto.ServiceRef destination =
				ServiceProto.ServiceRef.newBuilder().setComponentRef(localComponent).setServiceId("foobar").build();
		final MessageController messageController =
				new DefaultMessageController(
						capturingService.getServiceRef(),
						destination,
						ContentTypes.fromClass(TestProto.Foo.class));
		debugTransport.dispatch(
				messageController,
				rawMessageBytes(
						messageController.getContentType(),
						TestProto.Foo.newBuilder().setCurrentTime(System.currentTimeMillis()).build()));
		Assert.assertEquals(capturingService.getMessageCount(), 0);
	}

	@Test
	@DirtiesContext
	public void expiredMessage() {
		final MessageController messageController =
				new DefaultMessageController(
						capturingService.getServiceRef(),
						capturingService.getServiceRef(),
						ContentTypes.fromClass(TestProto.Foo.class),
						Optional.<byte[]> absent(),
						Optional.<byte[]> absent(),
						Optional.of(DateTime.now().minusDays(45)));
		debugTransport.dispatch(
				messageController,
				rawMessageBytes(
						messageController.getContentType(),
						TestProto.Foo.newBuilder().setCurrentTime(System.currentTimeMillis()).build()));
		Assert.assertEquals(capturingService.getMessageCount(), 0);
	}

	@Test
	@DirtiesContext
	@Ignore("This test will not pass because of the way protobufs handles unknown fields.")
	public void invalidMessagePayload() {
		final MessageController messageController =
				new DefaultMessageController(
						capturingService.getServiceRef(),
						capturingService.getServiceRef(),
						ContentTypes.fromClass(TestProto.Foo.class));
		final TestProto.Baz baz =
				TestProto.Baz
						.newBuilder()
						.setValue("ok")
						.addAttribute(TestProto.Baz.Attribute.newBuilder().setKey("foo").setValue("bar"))
						.build();
		final TestProto.Bar bar = TestProto.Bar.newBuilder().addBazi(baz).build();
		debugTransport.dispatch(messageController, rawMessageBytes(messageController.getContentType(), bar));
		Assert.assertEquals(capturingService.getMessageCount(), 0);
	}

	@Test
	@DirtiesContext
	public void unsupportedMessage() {
		final MessageController messageController =
				new DefaultMessageController(
						capturingService.getServiceRef(),
						capturingService.getServiceRef(),
						ContentTypes.fromClass(ServiceProto.ComponentRef.class));
		debugTransport.dispatch(messageController, rawMessageBytes(messageController.getContentType(), localComponent));
		Assert.assertEquals(capturingService.getMessageCount(), 0);
	}

	// NKG: This test is for coverage. What we should do is look at the logging
	// output for the following:
	// Error caught dispatching message.
	// java.lang.RuntimeException: Boom baby!
	@Test
	@DirtiesContext
	public void handleExplosion() {
		final MessageController messageController =
				new DefaultMessageController(
						explodingServiceRef,
						explodingServiceRef,
						ContentTypes.fromClass(TestProto.Foo.class));
		explodingdebugTransport.dispatch(
				messageController,
				rawMessageBytes(
						messageController.getContentType(),
						TestProto.Foo.newBuilder().setCurrentTime(System.currentTimeMillis()).build()));
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
