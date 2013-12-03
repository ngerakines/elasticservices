package org.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import org.socklabs.elasticservices.core.ServiceProto;

public class DefaultMessageController implements MessageController {

    private final ServiceProto.ServiceRef senderServiceRef;
    private final ServiceProto.ServiceRef destinationServiceRef;
    private final ServiceProto.ContentType contentType;
    private final Optional<byte[]> messageId;
    private final Optional<byte[]> correlationId;

    public DefaultMessageController(
            final ServiceProto.ServiceRef senderServiceRef,
            final ServiceProto.ServiceRef destinationServiceRef,
            final ServiceProto.ContentType contentType
    ) {
        this.senderServiceRef = senderServiceRef;
        this.destinationServiceRef = destinationServiceRef;
        this.contentType = contentType;
        this.messageId = Optional.absent();
        this.correlationId = Optional.absent();
    }

    public DefaultMessageController(
            final ServiceProto.ServiceRef senderServiceRef,
            final ServiceProto.ServiceRef destinationServiceRef,
            final ServiceProto.ContentType contentType,
            final Optional<byte[]> messageId,
            final Optional<byte[]> correlationId) {
        this.senderServiceRef = senderServiceRef;
        this.destinationServiceRef = destinationServiceRef;
        this.contentType = contentType;
        this.messageId = messageId;
        this.correlationId = correlationId;
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

    @Override
    public Optional<byte[]> getMessageId() {
        return messageId;
    }

    @Override
    public Optional<byte[]> getCorrelationId() {
        return correlationId;
    }

    @Override
    public String toString() {
        return "DefaultMessageController{" +
                "senderServiceRef=" + senderServiceRef +
                ", destinationServiceRef=" + destinationServiceRef +
                ", contentType=" + contentType +
                ", messageId=" + messageId +
                ", correlationId=" + correlationId +
                '}';
    }
}
