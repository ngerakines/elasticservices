package com.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.transport.Transport;
import com.socklabs.elasticservices.core.transport.TransportClient;
import com.socklabs.elasticservices.core.transport.TransportConsumer;

import java.util.List;

public interface ServiceRegistry {

	/**
	 * Register a local service implementation. When a service is registered
	 * with this method, the "ACTIVE" flag is set for the service.
	 */
	void registerService(final Service service, final Transport... transports);

	void deregisterService(final ServiceProto.ServiceRef serviceRef);

	/**
	 * Get, if it exists, a transport client associated with a given serviceref.
	 */
	Optional<TransportClient> transportClientForService(final ServiceProto.ServiceRef serviceRef);

	List<Ref> transportRefsForService(final ServiceProto.ServiceRef serviceRef);

	void initTransportClient(final ServiceProto.ServiceRef serviceRef, final Ref ref);

	/**
	 * Returns a list of all services known by the registry.
	 */
	List<ServiceProto.ServiceRef> getServices();

	/**
	 * Returns a list of all services known by the registry that match the
	 * given component ref.
	 */
	List<ServiceProto.ServiceRef> getServices(final ServiceProto.ComponentRef componentRef);

	/**
	 * Returns a list of all services known by the registry that match the
	 * given service id.
	 */
	List<ServiceProto.ServiceRef> getServices(final String id);

	/**
	 * Returns a list of all services known by the registry that match the
	 * given service id and have a component ref that matches the given
	 * site.
	 */
	List<ServiceProto.ServiceRef> getServices(final String site, final String id);

	/**
	 * Returns a list of all services known by the registry that match the
	 * given service id and have a component ref that matches the given
	 * site and cluster.
	 */
	List<ServiceProto.ServiceRef> getServices(final String site, final String cluster, final String id);

	List<Integer> getServiceFlags(final ServiceProto.ServiceRef serviceRef);

	/**
	 * Sends a message to the destination, ensuring the destination, sender
	 * and content type are recorded as meta-data.
	 */
	void sendMessage(
			final ServiceProto.ServiceRef destinationServiceRef,
			final ServiceProto.ServiceRef senderServiceRef,
			final AbstractMessage message,
			final ServiceProto.ContentType contentType);

	void sendMessage(final MessageController controller, final AbstractMessage message);

	void reply(
			final MessageController inboundMessageController,
			final ServiceProto.ServiceRef senderServiceRef,
			final AbstractMessage message,
			final ServiceProto.ContentType contentType);

	TransportConsumer newTransportConsumer();
}
