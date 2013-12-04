package org.socklabs.elasticservices.core.transport;

import com.googlecode.protobuf.format.JsonFormat;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.ContentTypes;
import org.socklabs.elasticservices.core.service.MessageController;

public abstract class AbstractTransport implements Transport {

	protected ServiceProto.ContentType getContentType(
			final MessageController controller, final com.google.protobuf.Message message) {
		return controller.getContentType();
	}

	protected <M extends com.google.protobuf.Message> byte[] rawMessageBytes(
			final ServiceProto.ContentType contentType, final M message) {
		if (contentType.getValue().equals(ContentTypes.CONTENT_TYPE_JSON)) {
			return JsonFormat.printToString(message).getBytes();
		}
		return message.toByteArray();
	}

}
