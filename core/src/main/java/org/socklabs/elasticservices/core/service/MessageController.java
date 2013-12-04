package org.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import org.socklabs.elasticservices.core.ServiceProto;

/**
 * A container used to represent message delivery meta-data.
 */
public interface MessageController {

	ServiceProto.ServiceRef getDestination();

	ServiceProto.ServiceRef getSender();

	ServiceProto.ContentType getContentType();

	Optional<byte[]> getMessageId();

	Optional<byte[]> getCorrelationId();

}
