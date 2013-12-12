package com.socklabs.elasticservices.http;

import com.google.protobuf.Message;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ProtobufView implements ElasticServiceView, View {

	@Override
	public String getContentType() {
		return "application/x-protobuf";
	}

	@Override
	public void render(
			final Map<String, ?> model,
			final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final Object responseObject = model.get(RESPONSE_OBJECT);
		if (responseObject != null && responseObject instanceof Message) {
			final Message message = (Message) responseObject;
			response.setContentType("application/x-protobuf;proto=" + message.getDescriptorForType().getFullName());
			response.getOutputStream().write(message.toByteArray());
			response.flushBuffer();
		}

	}

}
