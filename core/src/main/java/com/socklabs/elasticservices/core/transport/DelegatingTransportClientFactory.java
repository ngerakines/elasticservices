package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.socklabs.elasticservices.core.misc.Ref;

import java.util.List;

/**
 * A {@link TransportClientFactory} that uses an internal, ordered list of
 * transport client factories to attempt to create transport clients with.
 */
public class DelegatingTransportClientFactory implements TransportClientFactory {

	private List<TransportClientFactory> transportClientFactories;

	public DelegatingTransportClientFactory() {
		this.transportClientFactories = Lists.newArrayList();
	}

	public void addDelegate(final TransportClientFactory transportClientFactory) {
		transportClientFactories.add(transportClientFactory);
		transportClientFactories =
				Ordering.from(new TransportClientFactoryComparator()).sortedCopy(transportClientFactories);
	}

	@Override
	public Optional<TransportClient> get(final Ref ref) {
		for (final TransportClientFactory transportClientFactory : transportClientFactories) {
			final Optional<TransportClient> transportclientOptional = transportClientFactory.get(ref);
			if (transportclientOptional.isPresent()) {
				return transportclientOptional;
			}
		}
		return Optional.absent();
	}

}
