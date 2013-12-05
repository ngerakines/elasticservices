package com.socklabs.elasticservices.core.transport;

import com.googlecode.protobuf.format.JsonFormat;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.service.MessageController;

public abstract class AbstractTransport implements Transport {

	protected ServiceProto.ContentType getContentType(
			final MessageController controller,
			final com.google.protobuf.Message message) {
		return controller.getContentType();
	}

	protected <M extends com.google.protobuf.Message> byte[] rawMessageBytes(
			final ServiceProto.ContentType contentType,
			final M message) {
		if (contentType.getValue().equals(ContentTypes.CONTENT_TYPE_JSON)) {
			return JsonFormat.printToString(message).getBytes();
		}
		return message.toByteArray();
	}

}
