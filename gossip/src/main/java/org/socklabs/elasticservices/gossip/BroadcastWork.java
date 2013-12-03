package org.socklabs.elasticservices.gossip;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.ContentTypes;
import org.socklabs.elasticservices.core.service.ServiceRegistry;
import org.socklabs.elasticservices.core.transport.Transport;
import org.socklabs.elasticservices.core.work.AbstractWork;
import org.socklabs.elasticservices.core.work.Work;

import java.util.concurrent.TimeUnit;

public class BroadcastWork extends AbstractWork implements Work {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastWork.class);

    private final ServiceProto.ServiceRef gossipServiceRef;

    private final ServiceRegistry serviceRegistry;

    public BroadcastWork(
            final ServiceProto.ServiceRef gossipServiceRef, final ServiceRegistry serviceRegistry
    ) {
        super();
        this.gossipServiceRef = gossipServiceRef;
        this.serviceRegistry = serviceRegistry;
        setPhase(StandardPhase.STARTING);
    }

    @Override
    public String getId() {
        return "service:gossip:work:broadcast";
    }

    @Override
    public void run() {
        try {
            setPhase(StandardPhase.STARTED);

            final GossipServiceProto.ComponentOnline componentOnline = buildComponentOnlineMessage();
            final ServiceProto.ContentType contentType = ContentTypes.fromJsonClass(GossipServiceProto.ComponentOnline.class);
            serviceRegistry.sendMessage(gossipServiceRef, gossipServiceRef, componentOnline, contentType);

            int delay = 5;
            while (!isShuttingDown()) {
                final GossipServiceProto.ComponentStatus componentStatus = buildStatusMessage();
                serviceRegistry.<GossipServiceProto.ComponentStatus>sendMessage(gossipServiceRef,
                        gossipServiceRef,
                        componentStatus,
                        ContentTypes.fromJsonClass(GossipServiceProto.ComponentStatus.class));
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(delay));
                } catch (final InterruptedException e) {
                    LOGGER.error("interupted", e);
                    throw new RuntimeException("Sleep interupted.", e);
                }
                if (delay < 300) {
                    delay += 3;
                }
            }
            setPhase(StandardPhase.STOPPING);
            setPhase(StandardPhase.STOPPED);
        } catch (final Exception e) {
            LOGGER.error("Exception caught during broadcast work.", e);
        }
    }

    private GossipServiceProto.ComponentStatus buildStatusMessage() {
        final GossipServiceProto.ComponentStatus.Builder builder = GossipServiceProto.ComponentStatus.newBuilder();
        builder.setComponentRef(gossipServiceRef.getComponentRef());
        for (ServiceProto.ServiceRef serviceRef : serviceRegistry.getServices(gossipServiceRef.getComponentRef())) {
            final GossipServiceProto.ComponentService.Builder componentServiceBuilder = GossipServiceProto.ComponentService
                    .newBuilder();
            componentServiceBuilder.setServiceRef(serviceRef);
            final Optional<Transport> transportOptional = serviceRegistry.transportForService(serviceRef);
            if (transportOptional.isPresent()) {
                componentServiceBuilder.setTransportUrl(transportOptional.get().getRef());
            }
            builder.addServices(componentServiceBuilder);
        }
        return builder.build();
    }

    private GossipServiceProto.ComponentOnline buildComponentOnlineMessage() {
        final GossipServiceProto.ComponentOnline.Builder builder = GossipServiceProto.ComponentOnline
                .getDefaultInstance()
                .newBuilderForType();
        builder.setComponentRef(gossipServiceRef.getComponentRef());
        for (ServiceProto.ServiceRef serviceRef : serviceRegistry.getServices(gossipServiceRef.getComponentRef())) {
            final GossipServiceProto.ComponentService.Builder componentServiceBuilder = GossipServiceProto.ComponentService
                    .newBuilder();
            componentServiceBuilder.setServiceRef(serviceRef);
            final Optional<Transport> transportOptional = serviceRegistry.transportForService(serviceRef);
            if (transportOptional.isPresent()) {
                componentServiceBuilder.setTransportUrl(transportOptional.get().getRef());
            }
            builder.addServices(componentServiceBuilder);
        }
        return builder.build();
    }

}
