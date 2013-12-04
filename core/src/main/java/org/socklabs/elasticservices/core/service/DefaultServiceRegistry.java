package org.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.collection.CollectionUtils;
import org.socklabs.elasticservices.core.message.MessageFactory;
import org.socklabs.elasticservices.core.message.MessageUtils;
import org.socklabs.elasticservices.core.transport.Transport;
import org.socklabs.elasticservices.core.transport.TransportConsumer;
import org.socklabs.elasticservices.core.transport.TransportFactory;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class DefaultServiceRegistry implements ServiceRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceRegistry.class);

	// Used to create transports when services are discovered through gossip.
	private final TransportFactory transportFactory;

	private final ServiceProto.ComponentRef componentRef;

	// Simple mapping of service ref to service implementation, used for
	// routing from transports.
	private final ConcurrentMap<ServiceProto.ServiceRef, Service> services;

	// Simple mapping of service ref to transport, used to send messages to services
	private final Multimap<ServiceProto.ServiceRef, String> transportRefsByServiceRef;

	// An index of message factory by class/package.
	private final Multimap<String, MessageFactory> messageFactories;

	private final Multimap<String, ServiceProto.ServiceRef> transportServiceBindings;

	private final Map<String, Transport> transports;

	private final Set<ServiceProto.ServiceRef> serviceRefs;

	public DefaultServiceRegistry(
			final ServiceProto.ComponentRef componentRef,
			final TransportFactory transportFactory) {
		this.componentRef = componentRef;
		this.transportFactory = transportFactory;
		this.services = Maps.newConcurrentMap();
		this.transports = Maps.newConcurrentMap();
		this.messageFactories = ArrayListMultimap.create();
		this.transportServiceBindings = HashMultimap.create();
		this.serviceRefs = Sets.newHashSet();
		this.transportRefsByServiceRef = ArrayListMultimap.create();
	}

	@Override
	public synchronized void registerService(final Service service) {
		if (null != (services.putIfAbsent(service.getServiceRef(), service))) {
			throw new RuntimeException("Service with service ref already registered.");
		}
		serviceRefs.add(service.getServiceRef());
		for (final MessageFactory messageFactory : service.getMessageFactories()) {
			for (final String factoryPackage : messageFactory.supportedMessagePackages()) {
				messageFactories.put(factoryPackage, messageFactory);
			}
		}
	}

	@Override
	public synchronized void bindTransportToService(
			final ServiceProto.ServiceRef serviceRef,
			final Transport transport) {
		Preconditions.checkArgument(serviceRefs.contains(serviceRef));
		final String transportRef = transport.getRef();
		if (transportServiceBindings.containsEntry(transportRef, serviceRef)) {
			LOGGER.error(
					"Attempted to bind transport {} to service-ref {} when binding already exists.",
					transport.getRef(),
					serviceRef);
			return;
		}
		transports.put(transportRef, transport);
		transportServiceBindings.put(transportRef, serviceRef);
		transportRefsByServiceRef.put(serviceRef, transportRef);
		transport.addConsumer(new ServiceRegistryTransportConsumer(this));
	}

	private Transport getOrCreateTransport(final String transportRef) {
		if (transports.containsKey(transportRef)) {
			return transports.get(transportRef);
		}
		final Transport transport = transportFactory.get(transportRef);
		if (transport != null) {
			transports.put(transportRef, transport);
		}
		return transport;
	}

	@Override
	public synchronized Optional<Transport> transportForService(final ServiceProto.ServiceRef serviceRef) {
		final List<String> transportRefs = ImmutableList.copyOf(transportRefsByServiceRef.get(serviceRef));
		if (transportRefs.size() > 0) {
			final String transportRef = transportRefs.get(0);
			final Transport transport = getOrCreateTransport(transportRef);
			if (transport != null) {
				return Optional.of(transport);
			}
		}
		return Optional.absent();
	}

	@Override
	public synchronized void updateComponentServices(
			final ServiceProto.ComponentRef componentRef,
			final Map<ServiceProto.ServiceRef, String> services) {
		LOGGER.debug("Updating service information from gossip.");
		for (final Map.Entry<ServiceProto.ServiceRef, String> entry : services.entrySet()) {
			transportRefsByServiceRef.put(entry.getKey(), entry.getValue());
			serviceRefs.add(entry.getKey());
		}
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
		final Optional<Transport> transportOptional = transportForService(controller.getDestination());
		if (transportOptional.isPresent()) {
			final Transport transport = transportOptional.get();
			LOGGER.debug("Sending message ({}) to {}", message.getClass().getName(), MessageUtils.serviceRefToString(controller.getDestination()));
			transport.send(controller, message);
		}
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
