package com.socklabs.elasticservices.core.transport;

import org.springframework.core.Ordered;

import java.util.Comparator;

/**
 * Created by ngerakines on 12/23/13.
 */
public class TransportClientFactoryComparator implements Comparator<TransportClientFactory> {

	@Override
	public int compare(final TransportClientFactory o1, final TransportClientFactory o2) {
		final Integer o1v = getOrder(o1);
		final Integer o2v = getOrder(o2);
		return o1v.compareTo(o2v);
	}

	private Integer getOrder(final TransportClientFactory transportClientFactory) {
		if (transportClientFactory instanceof Ordered) {
			final Ordered ordered = (Ordered) transportClientFactory;
			return ordered.getOrder();
		}
		return 0;
	}

}