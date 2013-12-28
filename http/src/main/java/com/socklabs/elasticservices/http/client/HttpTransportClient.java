package com.socklabs.elasticservices.http.client;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import com.google.protobuf.AbstractMessage;
import com.googlecode.protobuf.format.JsonFormat;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.transport.TransportClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by ngerakines on 12/28/13.
 */
public class HttpTransportClient implements TransportClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransportClient.class);
	private static final BaseEncoding B16 = BaseEncoding.base16();

	private final Ref ref;
	private final String url;

	public HttpTransportClient(final Ref ref) {
		this.ref = ref;
		this.url = buildUrl(ref);
	}

	@Override
	public Ref getRef() {
		return ref;
	}

	@Override
	public void send(final MessageController messageController, final AbstractMessage message) {
		final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		try {
			final Map<String, Collection<String>> headers = buildHeaders(messageController);
			final AsyncHttpClient.BoundRequestBuilder requestBuilder = asyncHttpClient.preparePost(url);
			requestBuilder.setHeaders(headers);
			requestBuilder.setBody(messagePayload(messageController, message));
			final Future<Response> f = requestBuilder.execute();
			final Response r = f.get();
			final String responseBody = r.getResponseBody();
			if (!"OK".equals(responseBody)) {
				LOGGER.warn("Non-OK response received for HTTP transport client request.");
			}
		} catch (final IOException | ExecutionException | InterruptedException e) {
			LOGGER.error("Could not execute HTTP request.", e);
		}
	}

	private Map<String, Collection<String>> buildHeaders(final MessageController messageController) {
		final Map<String, Collection<String>> headers = Maps.newHashMap();
		headers.put(
				"X-DESTINATION",
				Lists.<String> newArrayList(JsonFormat.printToString(messageController.getDestination())));
		headers.put(
				"X-SENDER",
				Lists.<String> newArrayList(JsonFormat.printToString(messageController.getDestination())));

		headers.put("Content-Type", Lists.<String>newArrayList("application/x-protobuf"));
		headers.put(
				"X-CONTENT-TYPE",
				Lists.<String> newArrayList(JsonFormat.printToString(messageController.getContentType())));

		final Optional<byte[]> optionalMessageId = messageController.getMessageId();
		if (optionalMessageId.isPresent()) {
			headers.put("X-MESSAGE-ID", Lists.<String> newArrayList(B16.encode(optionalMessageId.get())));
		}
		final Optional<byte[]> optionalCorrelationId = messageController.getCorrelationId();
		if (optionalCorrelationId.isPresent()) {
			headers.put("X-CORRELATION-ID", Lists.<String> newArrayList(B16.encode(optionalCorrelationId.get())));
		}
		final Optional<DateTime> optionalExpires = messageController.getExpires();
		if (optionalExpires.isPresent()) {
			headers.put("X-EXPIRES", Lists.<String> newArrayList(String.valueOf(optionalExpires.get().getMillis())));
		}
		return headers;
	}

	private String buildUrl(final Ref ref) {
		final Optional<String> hostnameOptional = RefUtils.value(ref, "host");
		final Optional<String> portOptional = RefUtils.value(ref, "port");
		final Optional<String> serviceOptional = RefUtils.value(ref, "service");
		if (hostnameOptional.isPresent() && portOptional.isPresent() && serviceOptional.isPresent()) {
			final StringBuilder sb = new StringBuilder();
			final Integer port = Ints.tryParse(portOptional.get());
			if (port != null) {
				final String host = hostnameOptional.get();
				final String service = serviceOptional.get();
				sb.append("http://").append(host).append(":").append(port);
				sb.append("/").append(service);
				return sb.toString();
			}
		}
		throw new IllegalArgumentException("Valid HTTP ref required.");
	}

	private byte[] messagePayload(final MessageController messageController, final AbstractMessage message) {
		final ServiceProto.ContentType contentType = messageController.getContentType();
		if (ContentTypes.CONTENT_TYPE_JSON.equals(contentType.getValue())) {
			return JsonFormat.printToString(message).getBytes();
		} else if (ContentTypes.CONTENT_TYPE_PB.equals(contentType.getValue())) {
			return message.toByteArray();
		}
		return new byte[0];
	}

}
