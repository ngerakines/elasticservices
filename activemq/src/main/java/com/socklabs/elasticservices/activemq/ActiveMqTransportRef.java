package com.socklabs.elasticservices.activemq;

import com.google.common.base.Preconditions;
import com.socklabs.elasticservices.core.misc.Ref;

/**
 * Created by nick.gerakines on 1/9/14.
 */
public class ActiveMqTransportRef {

	private final Ref ref;
	private final String queue;

	public ActiveMqTransportRef(final Ref ref) {
		Preconditions.checkArgument(ref.getValue("queue").isPresent(), "routingKey missing");
		this.ref = ref;
		this.queue = ref.getValue("queue").get().getB().get();
	}

	public Ref getRef() {
		return ref;
	}

	public String getQueue() {
		return queue;
	}

}
