package com.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.ServiceProto;
import org.joda.time.DateTime;

/**
 * A container used to represent message delivery meta-data.
 */
public interface MessageController {

	ServiceProto.ServiceRef getDestination();

	ServiceProto.ServiceRef getSender();

	ServiceProto.ContentType getContentType();

	Optional<byte[]> getMessageId();

	Optional<byte[]> getCorrelationId();

	Optional<DateTime> getExpires();

}
