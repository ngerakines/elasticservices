package com.socklabs.elasticservices.core.misc;

/**
 * Created by ngerakines on 12/7/13.
 */
public class RefUtils {

	public static Ref rabbitMqTransportRef(final String exchange, final String routingKey, final String type) {
		final Ref.Builder refBuilder = Ref.builder("rabbitmq");
		refBuilder.addValue("exchange", exchange);
		refBuilder.addValue("routingKey", routingKey);
		refBuilder.addValue("type", type);
		return refBuilder.build();
	}

}
