package org.socklabs.elasticservices.core.transport;

import com.google.common.collect.Lists;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.ContentTypes;
import org.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

public class LocalTransport implements Transport {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalTransport.class);

	private final String id;

	private final List<TransportConsumer> consumers;

	public LocalTransport(final String id) {
		this.id = id;
		this.consumers = Lists.newArrayList();
	}

	@Override
	public void send(final MessageController messageController, final AbstractMessage message) {
		final ServiceProto.ContentType contentType = getContentType(messageController, message);
		if (contentType == null) {
			throw new RuntimeException("Could not get content type of message.");
		}
		try {
			final byte[] messageBytes = rawMessageBytes(contentType, message);
			for (final TransportConsumer consumer : consumers) {
				consumer.handleMessage(messageController, messageBytes);
			}
		} catch (final Exception e) {
			LOGGER.error("Exception caught publishing message:", e);
		}
	}

	@Override
	public void addConsumer(final TransportConsumer consumer) {
		consumers.add(consumer);
	}

	@Override
	public String getRef() {
		return "local://" + id;
	}

	private ServiceProto.ContentType getContentType(final MessageController controller, final Message message) {
		return controller.getContentType();
	}

	private <M extends Message> byte[] rawMessageBytes(
			final ServiceProto.ContentType contentType, final M message) {
		if (contentType.getValue().equals(ContentTypes.CONTENT_TYPE_JSON)) {
			return JsonFormat.printToString(message).getBytes();
		}
		return message.toByteArray();
	}

}
