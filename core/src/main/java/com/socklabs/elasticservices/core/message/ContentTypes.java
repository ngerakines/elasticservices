package com.socklabs.elasticservices.core.message;

import com.socklabs.elasticservices.core.ServiceProto;

public class ContentTypes {

	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String CONTENT_TYPE_PB = "application/x-protobuf";

	private ContentTypes() {
	}

    public static ServiceProto.ContentType fromClass(final Class<?> componentOnlineClass) {
        return fromClass(componentOnlineClass, CONTENT_TYPE_PB);
    }

	public static ServiceProto.ContentType fromClass(final Class<?> componentOnlineClass, final String contentType) {
		final ServiceProto.ContentType.Builder contentTypeBuilder = ServiceProto.ContentType.newBuilder();
		contentTypeBuilder.setValue(contentType);
		contentTypeBuilder.addAttribute(
				ServiceProto.ContentType
						.Attribute
						.newBuilder()
						.setKey("class")
						.setValue(componentOnlineClass.getName()));
		return contentTypeBuilder.build();
	}

}