package com.socklabs.elasticservices.core.message;

import com.google.common.base.Optional;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.service.MessageController;
import org.joda.time.DateTime;

import java.util.concurrent.Future;

/**
 * Created by ngerakines on 1/2/14.
 */
public interface ResponseManager {

	Future<Message> sendAndReceive(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass,
			final Optional<Expiration> expirationOptional);

	void handleMessage(final MessageController controller, final Message message);

	void clear(final DateTime clearPoint);

}
