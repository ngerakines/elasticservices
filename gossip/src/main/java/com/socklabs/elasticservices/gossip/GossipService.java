package com.socklabs.elasticservices.gossip;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServicePresenceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GossipService extends AbstractService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GossipService.class);

	private final List<ServicePresenceListener> servicePresenceListeners;

	private final List<MessageFactory> messageFactories;

	public GossipService(
			final MessageFactory messageFactory,
			final ServiceProto.ServiceRef serviceRef,
			final List<ServicePresenceListener> servicePresenceListeners) {
		super(serviceRef);
		this.servicePresenceListeners = servicePresenceListeners;

		this.messageFactories = ImmutableList.of(messageFactory);
	}

	@Override
	public List<MessageFactory> getMessageFactories() {
		return messageFactories;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (message instanceof GossipServiceProto.ComponentOnline) {
			// log.info("Component online message received: {}", message);
			// NKG: This isn't really a message that is acted on. It is
			// more informational than anything else.
		}
		if (message instanceof GossipServiceProto.ComponentStatus) {
			// log.info("Component status message received: {}",
			// message.toString());
			// TODO[NKG]: Pass this information the the service manager.
			// The service manager should then update any records that it
			// has for the component ref. If there are knew services, they
			// should be enabled by the service manager for use by
			// consumers of the service manager.

			final GossipServiceProto.ComponentStatus componentStatus = (GossipServiceProto.ComponentStatus) message;
			final Multimap<ServiceProto.ServiceRef, String> transports = ArrayListMultimap.create();
			final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags = HashMultimap.create();
			for (final GossipServiceProto.ComponentService componentService : componentStatus.getServicesList()) {
				for (final String transportUrl : componentService.getTransportUrlList()) {
					transports.put(componentService.getServiceRef(), transportUrl);
				}
				serviceFlags.putAll(getServiceRef(), componentService.getFlagList());
			}
			final ServiceProto.ComponentRef componentRef = componentStatus.getComponentRef();
			for (final ServicePresenceListener servicePresenceListener : servicePresenceListeners) {
				try {
					servicePresenceListener.updateComponentServices(componentRef, transports, serviceFlags);
				} catch (final RuntimeException e) {
					LOGGER.error(
							"Gossip service caught runtime exception attempting to notify service presence listener.",
							e);
				}
			}
		}
	}

}
