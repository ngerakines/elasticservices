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

	public GossipService(
			final MessageFactory messageFactory,
			final ServiceProto.ServiceRef serviceRef,
			final List<ServicePresenceListener> servicePresenceListeners) {
		super(serviceRef, ImmutableList.of(messageFactory));
		this.servicePresenceListeners = servicePresenceListeners;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (messageHasExpired(controller)) {
			return;
		}
		if (message instanceof GossipServiceProto.ComponentOnline) {
			final GossipServiceProto.ComponentOnline componentOnline = (GossipServiceProto.ComponentOnline) message;
			processComponentStatus(componentOnline.getComponentRef(), componentOnline.getServicesList());
		}
		if (message instanceof GossipServiceProto.ComponentStatus) {
			final GossipServiceProto.ComponentStatus componentStatus = (GossipServiceProto.ComponentStatus) message;
			processComponentStatus(componentStatus.getComponentRef(), componentStatus.getServicesList());
		}
	}

	private void processComponentStatus(
			final ServiceProto.ComponentRef componentRef,
			final List<GossipServiceProto.ComponentService> servicesList) {
		final Multimap<ServiceProto.ServiceRef, String> transports = ArrayListMultimap.create();
		final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags = HashMultimap.create();
		for (final GossipServiceProto.ComponentService componentService : servicesList) {
			for (final String transportUrl : componentService.getTransportUrlList()) {
				transports.put(componentService.getServiceRef(), transportUrl);
			}
			serviceFlags.putAll(getServiceRef(), componentService.getFlagList());
		}
		for (final ServicePresenceListener servicePresenceListener : servicePresenceListeners) {
			try {
				servicePresenceListener.updateComponentServices(componentRef, transports, serviceFlags);
			} catch (final RuntimeException e) {
				LOGGER.error("Exception caught attempting to notify service presence listeners.", e);
			}
		}
	}

}
