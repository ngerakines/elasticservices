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

}
