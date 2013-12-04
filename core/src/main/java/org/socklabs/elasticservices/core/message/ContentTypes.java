package org.socklabs.elasticservices.core.message;

import org.socklabs.elasticservices.core.ServiceProto;

public class ContentTypes {

	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String CONTENT_TYPE_PB = "application/x-protobuf";

	private ContentTypes() {
	}

	public static ServiceProto.ContentType fromJsonClass(final Class<?> componentOnlineClass) {
		final ServiceProto.ContentType.Builder contentTypeBuilder = ServiceProto.ContentType.newBuilder();
		contentTypeBuilder.addAttribute(
				ServiceProto.ContentType
						.Attribute
						.newBuilder()
						.setKey("class")
						.setValue(componentOnlineClass.getName()));
		return contentTypeBuilder.build();
	}

}