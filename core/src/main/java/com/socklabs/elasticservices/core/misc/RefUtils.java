package com.socklabs.elasticservices.core.misc;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.collection.Pair;

/**
 * Created by ngerakines on 12/7/13.
 */
public class RefUtils {

	public static Ref rabbitMqTransportRef(final String exchange, final String routingKey, final String type) {
		final Ref.Builder refBuilder = Ref.builder("rabbitmq");
		refBuilder.addValue("exchange", exchange);
		refBuilder.addValue("routingKey", routingKey);
		refBuilder.addValue("type", type);
		refBuilder.addValue("order", "5000");
		return refBuilder.build();
	}

	public static Ref httpTransportRef(final String host, final int port, final String service) {
		final Ref.Builder refBuilder = Ref.builder("http");
		refBuilder.addValue("host", host);
		refBuilder.addValue("port", String.valueOf(port));
		refBuilder.addValue("service", service);
		refBuilder.addValue("order", "4000");
		return refBuilder.build();
	}

	public static Optional<String> value(final Ref ref, final String key) {
		final Optional<Pair<String, Optional<String>>> pairOptional = ref.getValue(key);
		if (pairOptional.isPresent()) {
			final Pair<String, Optional<String>> pair = pairOptional.get();
			return pair.getB();
		}
		return Optional.absent();
	}

}
