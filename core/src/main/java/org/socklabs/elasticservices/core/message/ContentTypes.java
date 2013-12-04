package org.socklabs.elasticservices.core.message;

import org.apache.commons.lang.NotImplementedException;
import org.socklabs.elasticservices.core.ServiceProto;

public class ContentTypes {

	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String CONTENT_TYPE_PB = "application/x-protobuf";

	private ContentTypes() {
	}

	public static ServiceProto.ContentType fromString(final String input) {
		throw new NotImplementedException("ContentTypes.fromString is not implemented.");
	}

	public static ServiceProto.ContentType fromJson(final String json) {
		throw new NotImplementedException("ContentTypes.fromJson is not implemented.");
	}

	public static ServiceProto.ContentType fromBytes(final byte[] input) {
		throw new NotImplementedException("ContentTypes.fromBytes is not implemented.");
	}

	public static ServiceProto.ContentType fromJsonClass(final Class<?> componentOnlineClass) {
		return ServiceProto.ContentType.newBuilder().setValue(CONTENT_TYPE_JSON).addAttribute(
				ServiceProto.ContentType
						.Attribute
						.newBuilder()
						.setKey("class")
						.setValue(componentOnlineClass.getName())).build();
	}

}