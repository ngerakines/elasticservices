package com.socklabs.elasticservices.http;

import com.google.common.base.Optional;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by ngerakines on 12/12/13.
 */
public class JsonView implements ElasticServiceView, View {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonView.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final Optional<String> callback;

	public JsonView() {
		this(Optional.<String>absent());
	}

	public JsonView(final String callback) {
		this(Optional.of(callback));
	}

	public JsonView(final Optional<String> callback) {
		// NKG: The value of this callback should be scrubbed to only include alphanumerics and ".$"
		this.callback = callback;
	}

	@Override
	public String getContentType() {
		if (callback.isPresent()) {
			return "application/javascript;charset=utf-8";
		}
		return "application/json;charset=utf-8";
	}

	@Override
	public void render(
			final Map<String, ?> model,
			final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		response.setContentType(getContentType());
		response.setCharacterEncoding("UTF-8");

		final Writer writer = response.getWriter();

		if (callback.isPresent()) {
			writer.write(callback.get());
			writer.write("(");
			writeModel(writer, model);
			writer.write(");");
		} else {
			writeModel(writer, model);
		}
	}

	private void writeModel(final Writer writer, final Map<String, ?> model) throws IOException {
		final Object responseObject = model.get(RESPONSE_OBJECT);
		if (responseObject == null) {
			LOGGER.warn("Attempted to render null response object.");
			return;
		}
		if (responseObject instanceof Message) {
			writer.write(JsonFormat.printToString((Message) responseObject));
		} else {
			writer.write(OBJECT_MAPPER.writeValueAsString(responseObject));
		}
	}

}
