package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Preconditions;
import com.socklabs.elasticservices.core.misc.Ref;

/**
* Created by ngerakines on 12/7/13.
*/
class RabbitMqTransportRef {
	private final Ref ref;
	private final String exchange;
	private final String routingKey;
	private final String type;

	RabbitMqTransportRef(final String url) {
		this(Ref.builderFromUri(url).build());
	}

	RabbitMqTransportRef(final Ref ref) {
		Preconditions.checkArgument(ref.getValue("exchange").isPresent(), "exchange missing");
		Preconditions.checkArgument(ref.getValue("routingKey").isPresent(), "routingKey missing");
		Preconditions.checkArgument(ref.getValue("type").isPresent(), "type missing");
		this.ref = ref;
		this.exchange = ref.getValue("exchange").get().getB().get();
		this.routingKey = ref.getValue("routingKey").get().getB().get();
		this.type = ref.getValue("type").get().getB().get();
	}

	public Ref getRef() {
		return ref;
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
