package com.socklabs.elasticservices.core.message;

import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.service.MessageController;

/**
 * Created by ngerakines on 1/2/14.
 */
public interface ResponseManager {

	AbstractFuture<Message> sendAndReceive(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass,
			final Optional<Expiration> expirationOptional);

	AbstractFuture<Message> sendAndReceive(final MessageController messageController, final AbstractMessage message);

	void handleMessage(final MessageController controller, final Message message);

	void clear(final DateTime clearPoint);

}
