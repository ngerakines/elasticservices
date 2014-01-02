package com.socklabs.elasticservices.core.message;

import com.socklabs.elasticservices.core.ServiceProto;

public class ContentTypes {

	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String CONTENT_TYPE_PB = "application/x-protobuf";

	private ContentTypes() {
	}

	@Deprecated
	public static ServiceProto.ContentType fromJsonClass(final Class<?> componentOnlineClass) {
		final ServiceProto.ContentType.Builder contentTypeBuilder = ServiceProto.ContentType.newBuilder();
		contentTypeBuilder.setValue(CONTENT_TYPE_JSON);
		contentTypeBuilder.addAttribute(
				ServiceProto.ContentType
						.Attribute
						.newBuilder()
						.setKey("class")
						.setValue(componentOnlineClass.getName()));
		return contentTypeBuilder.build();
	}

	public static ServiceProto.ContentType fromClass(final Class<?> componentOnlineClass) {
		final ServiceProto.ContentType.Builder contentTypeBuilder = ServiceProto.ContentType.newBuilder();
		contentTypeBuilder.setValue(CONTENT_TYPE_PB);
		contentTypeBuilder.addAttribute(
				ServiceProto.ContentType
						.Attribute
						.newBuilder()
						.setKey("class")
						.setValue(componentOnlineClass.getName()));
		return contentTypeBuilder.build();
	}

}