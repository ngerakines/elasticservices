package org.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import com.google.protobuf.AbstractMessage;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.transport.Transport;

import java.util.List;
import java.util.Map;

public interface ServiceRegistry {

	/**
	 * Register a local service implementation.
	 */
	void registerService(final Service service);

	/**
	 * Binds incoming messages of a given transport to be processed by the
	 * service implementation associated with the provided service ref.
	 */
	void bindTransportToService(final ServiceProto.ServiceRef serviceRef, final Transport transport);

	/**
	 * Get, if it exists, a transport associated with a given serviceref.
	 */
	Optional<Transport> transportForService(final ServiceProto.ServiceRef serviceRef);

	/**
	 * Called to do auto discovery via gossip of services associated with a component.
	 */
	void updateComponentServices(
			final ServiceProto.ComponentRef componentRef, final Map<ServiceProto.ServiceRef, String> services);

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

	/**
	 * Sends a message to the destination, ensuring the destination, sender
	 * and content type are recorded as meta-data.
	 */
	void sendMessage(
			final ServiceProto.ServiceRef destinationServiceRef,
			final ServiceProto.ServiceRef senderServiceRef,
			final AbstractMessage message,
			final ServiceProto.ContentType contentType);

	void sendMessage(
			final MessageController controller, final AbstractMessage message);

}
