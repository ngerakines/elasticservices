package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Preconditions;
import com.socklabs.elasticservices.core.misc.Ref;

/**
* Created by ngerakines on 12/7/13.
*/
class RabbitMqTransportRef {
	private final String url;
	private final String exchange;
	private final String routingKey;
	private final String type;

	RabbitMqTransportRef(final String url) {
		final Ref ref = Ref.builderFromUri(url).build();
		Preconditions.checkArgument(
				ref.getValue("exchange").isPresent()
						&& ref.getValue("routingKey").isPresent()
						&& ref.getValue("type").isPresent());
		this.url = url;
		this.exchange = ref.getValue("exchange").get().getB().get();
		this.routingKey = ref.getValue("routingKey").get().getB().get();
		this.type = ref.getValue("type").get().getB().get();
	}

	public String getUrl() {
		return url;
	}

	public String getExchange() {
		return exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public String getType() {
		return type;
	}

}
