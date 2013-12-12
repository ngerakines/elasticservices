package com.socklabs.elasticservices.gossip;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;

import java.util.List;
import java.util.Map;

public class GossipService extends AbstractService {

	private final ServiceRegistry serviceRegistry;

	private final GossipMessageFactory gossipMessageFactory;

	public GossipService(
			final ServiceProto.ServiceRef serviceRef, final ServiceRegistry serviceRegistry) {
		super(serviceRef);
		this.serviceRegistry = serviceRegistry;

		this.gossipMessageFactory = new GossipMessageFactory();
	}

	@Override
	public List<MessageFactory> getMessageFactories() {
		return ImmutableList.<MessageFactory>of(gossipMessageFactory);
	}

	@Override
	public void handleMessage(
			final MessageController controller, final Message message) {
		if (message instanceof GossipServiceProto.ComponentOnline) {
			//log.info("Component online message received: {}", message);
			// NKG: This isn't really a message that is acted on. It is
			// more informational than anything else.
		}
		if (message instanceof GossipServiceProto.ComponentStatus) {
			//log.info("Component status message received: {}", message.toString());
			// TODO[NKG]: Pass this information the the service manager.
			// The service manager should then update any records that it
			// has for the component ref. If there are knew services, they
			// should be enabled by the service manager for use by
			// consumers of the service manager.

			final GossipServiceProto.ComponentStatus componentStatus = (GossipServiceProto.ComponentStatus) message;
			final Map<ServiceProto.ServiceRef, String> transports = Maps.newHashMap();
			final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags = HashMultimap.create();
			for (final GossipServiceProto.ComponentService componentService : componentStatus.getServicesList()) {
				if (componentService.hasTransportUrl()) {
					transports.put(componentService.getServiceRef(), componentService.getTransportUrl());
				}
				serviceFlags.putAll(getServiceRef(), componentService.getFlagList());
			}
			final ServiceProto.ComponentRef componentRef = componentStatus.getComponentRef();
			serviceRegistry.updateComponentServices(componentRef, transports, serviceFlags);
		}
	}

}
