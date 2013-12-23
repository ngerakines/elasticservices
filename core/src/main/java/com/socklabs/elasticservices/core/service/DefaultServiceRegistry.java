package com.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.collection.CollectionUtils;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.message.MessageUtils;
import com.socklabs.elasticservices.core.misc.OrderingRefComparator;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.transport.TransportClient;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import com.socklabs.elasticservices.core.transport.TransportConsumer;
import com.socklabs.servo.ext.CounterCacheCompositeMonitor;
import com.socklabs.servo.ext.KeyIncrementable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class DefaultServiceRegistry implements ServiceRegistry, ServicePresenceListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceRegistry.class);

	// Used to create transports when services are discovered through gossip.
	private final TransportClientFactory transportClientFactory;

	private final ServiceProto.ComponentRef componentRef;

	// Simple mapping of service ref to service implementation, used for
	// routing from transports.
	private final ConcurrentMap<ServiceProto.ServiceRef, Service> services;
	private final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags;

	// Simple mapping of service ref to transport, used to send messages to services
	private final Multimap<ServiceProto.ServiceRef, Ref> transportRefsByServiceRef;

	// An index of message factory by class/package.
	private final Multimap<String, MessageFactory> messageFactories;

	private final Map<Ref, TransportClient> transportClients;

	private final Set<ServiceProto.ServiceRef> serviceRefs;
	private final Multimap<ServiceProto.ServiceRef, Transport> serviceTransports;

	private final KeyIncrementable<String> senderCounters;
	private final KeyIncrementable<String> destinationCounters;

	public DefaultServiceRegistry(
			final ServiceProto.ComponentRef componentRef,
			final TransportClientFactory transportClientFactory) {
		this.componentRef = componentRef;
		this.transportClientFactory = transportClientFactory;

		this.services = Maps.newConcurrentMap();
		this.messageFactories = ArrayListMultimap.create();
		this.serviceTransports = ArrayListMultimap.create();

		this.serviceFlags = HashMultimap.create();
		this.serviceRefs = Sets.newHashSet();
		this.transportRefsByServiceRef = ArrayListMultimap.create();
		this.transportClients = Maps.newHashMap();

		this.senderCounters = new CounterCacheCompositeMonitor<>("senderCounters");
		this.destinationCounters = new CounterCacheCompositeMonitor<>("destinationCounters");
	}

	@Override
	public synchronized void registerService(final Service service, final Transport... transports) {
		if (null != (services.putIfAbsent(service.getServiceRef(), service))) {
			throw new RuntimeException("Service with service ref already registered.");
		}
		service.setFlag(ServiceProto.ServiceFlags.ACTIVE_VALUE);
		serviceRefs.add(service.getServiceRef());
		for (final MessageFactory messageFactory : service.getMessageFactories()) {
			for (final String factoryPackage : messageFactory.supportedMessagePackages()) {
				messageFactories.put(factoryPackage, messageFactory);
			}
		}
		for (final Transport transport : transports) {
			transport.addConsumer(new ServiceRegistryTransportConsumer(this));
			serviceTransports.put(service.getServiceRef(), transport);
			transportRefsByServiceRef.put(service.getServiceRef(), transport.getRef());
			serviceRefs.add(service.getServiceRef());
		}
	}

	@Override
	public synchronized void deregisterService(final ServiceProto.ServiceRef serviceRef) {
		final List<Ref> transportRefs = transportClientRefs(serviceRef);
		transportRefsByServiceRef.removeAll(transportRefs);
		for (final Ref transportRef : transportRefs) {
			transportClients.remove(transportRef);
		}
	}

	@Override
	public synchronized Optional<TransportClient> transportClientForService(final ServiceProto.ServiceRef serviceRef) {
		LOGGER.debug("Requested transport client for serviceRef {}.", MessageUtils.serviceRefToString(serviceRef));
		final List<Ref> transportRefs = transportClientRefs(serviceRef);
		if (transportRefs.size() == 0) {
			return Optional.absent();
		}
		final Ref transportRef = transportRefs.get(0);
		final TransportClient existingTransportClient = transportClients.get(transportRef);
		if (existingTransportClient != null) {
			return Optional.of(existingTransportClient);
		}
		final Optional<TransportClient> transportClientOptional = transportClientFactory.get(transportRef);
		if (transportClientOptional.isPresent()) {
			LOGGER.debug("Storing transport client for ref {}.", transportRef.toString());
			transportClients.put(transportRef, transportClientOptional.get());
			return transportClientOptional;
		}
		return Optional.absent();
	}

	@Override
	public synchronized List<Ref> transportRefsForService(final ServiceProto.ServiceRef serviceRef) {
		return transportClientRefs(serviceRef);
	}

	private List<Ref> transportClientRefs(final ServiceProto.ServiceRef serviceRef) {
		final List<Ref> refs = Lists.newArrayList(transportRefsByServiceRef.get(serviceRef));
		if (refs.size() > 1) {
			return Ordering.from(new OrderingRefComparator()).greatestOf(refs, 1);
		}
		return refs;
	}

	@Override
	public void updateComponentServices(
			final ServiceProto.ComponentRef componentRef,
			final Multimap<ServiceProto.ServiceRef, String> services,
			final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags) {
		LOGGER.debug("Updating service information from gossip.");
		for (final Map.Entry<ServiceProto.ServiceRef, String> entry : services.entries()) {
			LOGGER.debug(
					"Received transport ref uri {} for {}",
					entry.getValue(),
					MessageUtils.serviceRefToString(entry.getKey()));
			initTransportClient(entry.getKey(), Ref.builderFromUri(entry.getValue()).build());
		}
	}

	@Override
	public synchronized void initTransportClient(final ServiceProto.ServiceRef serviceRef, final Ref ref) {
		transportRefsByServiceRef.put(serviceRef, ref);
		serviceRefs.add(serviceRef);
	}

	@Override
	public synchronized List<ServiceProto.ServiceRef> getServices() {
		return Ordering.from(new ServiceRefComparator()).sortedCopy(ImmutableList.copyOf(serviceRefs));
	}

	@Override
	public synchronized List<ServiceProto.ServiceRef> getServices(final ServiceProto.ComponentRef componentRef) {
		return Ordering.from(new ServiceRefComparator())
				.sortedCopy(
						ImmutableList.copyOf(
								Iterables.filter(
										serviceRefs,
										new ComponentRefServiceRefsFilter(componentRef))));
	}

	@Override
	public synchronized List<ServiceProto.ServiceRef> getServices(final String id) {
		return Ordering.from(new ServiceRefComparator())
				.sortedCopy(
						ImmutableList.copyOf(
								Iterables.filter(
										serviceRefs,
										new IdServiceRefsFilter(id))));
	}

	@Override
	public synchronized List<ServiceProto.ServiceRef> getServices(final String site, final String id) {
		final Predicate<ServiceProto.ServiceRef> predicate = new SiteIdServiceRefsFilter(site, id);
		Iterable<ServiceProto.ServiceRef> results = Iterables.filter(serviceRefs, predicate);
		return Ordering.from(new ServiceRefComparator()).sortedCopy(results);
	}

	@Override
	public synchronized List<ServiceProto.ServiceRef> getServices(
			final String site,
			final String cluster,
			final String id) {
		return Ordering.from(new ServiceRefComparator()).sortedCopy(
				ImmutableList.copyOf(
						Iterables.filter(
								serviceRefs,
								new SiteClusterIdServiceRefsFilter(site, cluster, id))));
	}

	@Override
	public List<Integer> getServiceFlags(final ServiceProto.ServiceRef serviceRef) {
		final Service service = services.get(serviceRef);
		if (service != null) {
			return service.getFlags();
		}
		return ImmutableList.copyOf(serviceFlags.get(serviceRef));
	}

	@Override
	public synchronized void sendMessage(
			final ServiceProto.ServiceRef destinationServiceRef,
			final ServiceProto.ServiceRef senderServiceRef,
			final AbstractMessage message,
			final ServiceProto.ContentType contentType) {
		final MessageController controller = new DefaultMessageController(
				senderServiceRef,
				destinationServiceRef,
				contentType);
		sendMessage(controller, message);
	}

	@Override
	public synchronized void sendMessage(final MessageController controller, final AbstractMessage message) {
		senderCounters.incr(MessageUtils.serviceRefToString(controller.getSender()));
		destinationCounters.incr(MessageUtils.serviceRefToString(controller.getDestination()));
		final Optional<TransportClient> transportClientOptional = transportClientForService(controller.getDestination());
		if (transportClientOptional.isPresent()) {
			final TransportClient transportClient = transportClientOptional.get();
			LOGGER.debug(
					"Sending message ({}) to {}", message.getClass().getName(), MessageUtils.serviceRefToString(
					controller.getDestination()));
			transportClient.send(controller, message);
		}
	}

	@Override
	public void reply(
			final MessageController inboundMessageController,
			final ServiceProto.ServiceRef senderServiceRef,
			final AbstractMessage message,
			final ServiceProto.ContentType contentType) {

		final MessageController outboundController = new DefaultMessageController(
				senderServiceRef,
				inboundMessageController.getSender(),
				contentType,
				Optional.of(MessageUtils.randomMessageId(24)),
				inboundMessageController.getMessageId());
		sendMessage(outboundController, message);
	}

	private void dispatchMessage(final MessageController messageController, final byte[] rawMessage) {
		LOGGER.debug("Dispatching message {}", messageController.getContentType().getValue());
		final ServiceProto.ServiceRef serviceRef = destinationOf(messageController);
		final Service service = services.get(serviceRef);
		if (service == null) {
			return;
		}
		final Optional<? extends Message> messageOptional = composeMessage(messageController, rawMessage);
		if (!messageOptional.isPresent()) {
			return;
		}
		final Message message = messageOptional.get();
		try {
			service.handleMessage(messageController, message);
		} catch (final Exception e) {
			LOGGER.error("Error caught dispatching message.", e);
		}
	}

	private ServiceProto.ServiceRef destinationOf(final MessageController messageController) {
		final ServiceProto.ServiceRef serviceRef = messageController.getDestination();
		if (serviceRef != null && "gossip".equals(serviceRef.getServiceId())) {
			final List<ServiceProto.ServiceRef> localServiceRefs = getServices(componentRef);
			for (final ServiceProto.ServiceRef localServiceRef : localServiceRefs) {
				if (localServiceRef.getServiceId().equals("gossip")) {
					return localServiceRef;
				}
			}
		}
		return serviceRef;
	}

	private Optional<? extends Message> composeMessage(
			final MessageController messageController,
			final byte[] rawMessage) {
		final ServiceProto.ContentType contentType = messageController.getContentType();
		if (contentType != null) {
			final Optional<String> classNameOptional = CollectionUtils.firstAttributeValue(
					contentType.getAttributeList(),
					"class");
			if (classNameOptional.isPresent()) {
				final String className = classNameOptional.get();
				final List<MessageFactory> messageFactories = ImmutableList.copyOf(
						this.messageFactories.get(className));
				for (final MessageFactory messageFactory : messageFactories) {
					final Optional<? extends Message> fabricMessageOptional = messageFactory.get(
							messageController,
							rawMessage);
					if (fabricMessageOptional.isPresent()) {
						return fabricMessageOptional;
					}
				}
			}
		}
		return Optional.absent();
	}

	private static class ServiceRegistryTransportConsumer implements TransportConsumer {

		private final DefaultServiceRegistry serviceRegistry;

		private ServiceRegistryTransportConsumer(final DefaultServiceRegistry serviceRegistry) {
			this.serviceRegistry = serviceRegistry;
		}

		@Override
		public void handleMessage(final MessageController messageController, final byte[] rawMessage) {
			serviceRegistry.dispatchMessage(messageController, rawMessage);
		}
	}

	private static class ComponentRefServiceRefsFilter implements Predicate<ServiceProto.ServiceRef> {

		private final ServiceProto.ComponentRef componentRef;

		private ComponentRefServiceRefsFilter(final ServiceProto.ComponentRef componentRef) {
			this.componentRef = componentRef;
		}

		@Override
		public boolean apply(@Nullable final ServiceProto.ServiceRef input) {
			return input != null && componentRef.equals(input.getComponentRef());
		}
	}

	private static class IdServiceRefsFilter implements Predicate<ServiceProto.ServiceRef> {

		private final String serviceId;

		private IdServiceRefsFilter(final String serviceId) {
			this.serviceId = serviceId;
		}

		@Override
		public boolean apply(@Nullable final ServiceProto.ServiceRef input) {
			return input != null && serviceId.equals(input.getServiceId());
		}
	}

	private static class SiteIdServiceRefsFilter implements Predicate<ServiceProto.ServiceRef> {

		private final String site;

		private final String serviceId;

		private SiteIdServiceRefsFilter(final String site, final String serviceId) {
			this.site = site;
			this.serviceId = serviceId;
		}

		@Override
		public boolean apply(@Nullable final ServiceProto.ServiceRef input) {
			return input != null && site.equals(
					input.getComponentRef()
							.getSite()) && serviceId.equals(input.getServiceId());
		}
	}

	private static class SiteClusterIdServiceRefsFilter implements Predicate<ServiceProto.ServiceRef> {

		private final String site;

		private final String cluster;

		private final String serviceId;

		private SiteClusterIdServiceRefsFilter(final String site, final String cluster, final String serviceId) {
			this.site = site;
			this.cluster = cluster;
			this.serviceId = serviceId;
		}

		@Override
		public boolean apply(@Nullable final ServiceProto.ServiceRef input) {
			return input != null &&
					site.equals(input.getComponentRef().getSite()) &&
					cluster.equals(input.getComponentRef().getCluster()) &&
					serviceId.equals(input.getServiceId());
		}
	}

	private static class ComponentRefComparator implements Comparator<ServiceProto.ComponentRef> {

		@Override
		public int compare(final ServiceProto.ComponentRef o1, final ServiceProto.ComponentRef o2) {
			if (o1.getSite().equals(o2.getSite())) {
				if (o1.getCluster().equals(o2.getCluster())) {
					return o1.getComponentId().compareTo(o2.getComponentId());
				}
				return o1.getCluster().compareTo(o2.getCluster());
			}
			return o1.getSite().compareTo(o2.getSite());
		}
	}

	private static class ServiceRefComparator implements Comparator<ServiceProto.ServiceRef> {

		@Override
		public int compare(final ServiceProto.ServiceRef o1, final ServiceProto.ServiceRef o2) {
			if (o1.getComponentRef().equals(o2.getComponentRef())) {
				return o1.getServiceId().compareTo(o2.getServiceId());
			}
			return new ComponentRefComparator().compare(o1.getComponentRef(), o2.getComponentRef());
		}
	}

}
