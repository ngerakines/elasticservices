package org.socklabs.elasticservices.core.service;

import org.socklabs.elasticservices.core.ServiceProto;

public class DefaultMessageController implements MessageController {

    private final ServiceProto.ServiceRef senderServiceRef;

    private final ServiceProto.ServiceRef destinationServiceRef;

    private final ServiceProto.ContentType contentType;

    public DefaultMessageController(
            final ServiceProto.ServiceRef senderServiceRef,
            final ServiceProto.ServiceRef destinationServiceRef,
            final ServiceProto.ContentType contentType
    ) {
        this.senderServiceRef = senderServiceRef;
        this.destinationServiceRef = destinationServiceRef;
        this.contentType = contentType;
    }

    @Override
    public ServiceProto.ServiceRef getDestination() {
        return destinationServiceRef;
    }

    @Override
    public ServiceProto.ServiceRef getSender() {
        return senderServiceRef;
    }

    @Override
    public ServiceProto.ContentType getContentType() {
        return contentType;
    }

}
