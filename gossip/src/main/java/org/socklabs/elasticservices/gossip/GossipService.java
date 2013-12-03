package org.socklabs.elasticservices.gossip;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.MessageFactory;
import org.socklabs.elasticservices.core.service.MessageController;
import org.socklabs.elasticservices.core.service.Service;
import org.socklabs.elasticservices.core.service.ServiceRegistry;

import java.util.List;
import java.util.Map;

public class GossipService implements Service {

    private final ServiceProto.ServiceRef serviceRef;

    private final ServiceRegistry serviceRegistry;

    private final GossipMessageFactory gossipMessageFactory;

    public GossipService(
            final ServiceProto.ServiceRef serviceRef, final ServiceRegistry serviceRegistry
    ) {
        this.serviceRef = serviceRef;
        this.serviceRegistry = serviceRegistry;

        this.gossipMessageFactory = new GossipMessageFactory();
    }

    @Override
    public ServiceProto.ServiceRef getServiceRef() {
        return serviceRef;
    }

    @Override
    public List<MessageFactory> getMessageFactories() {
        return ImmutableList.<MessageFactory>of(gossipMessageFactory);
    }


    @Override
    public void handleMessage(
            final MessageController controller,
            final Message message
    ) {
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
            for (final GossipServiceProto.ComponentService componentService : componentStatus.getServicesList()) {
                if (componentService.hasTransportUrl()) {
                    transports.put(componentService.getServiceRef(), componentService.getTransportUrl());
                }
            }
            final ServiceProto.ComponentRef componentRef = componentStatus.getComponentRef();
            serviceRegistry.updateComponentServices(componentRef, transports);
        }
    }

}
