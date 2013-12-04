package org.socklabs.elasticservices.core.edge;

import com.google.common.base.Optional;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import org.joda.time.DateTime;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.Expiration;
import org.socklabs.elasticservices.core.service.MessageController;

import java.util.concurrent.Future;

public interface EdgeManager {

	Future<Message> execute(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass,
			final Optional<Expiration> expirationOptional);

	void handleMessage(final MessageController controller, final Message message);

	public void clear(final DateTime clearPoint);

}
