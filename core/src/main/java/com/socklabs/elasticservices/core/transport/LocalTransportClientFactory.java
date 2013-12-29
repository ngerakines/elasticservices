package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.misc.RefUtils;
import com.socklabs.elasticservices.core.service.Service;

import java.util.Map;

/**
 * Created by ngerakines on 12/29/13.
 */
public class LocalTransportClientFactory implements TransportClientFactory {

	private final Map<String, Service> services;

	public LocalTransportClientFactory() {
		this.services = Maps.newHashMap();
	}

	@Override
	public Optional<TransportClient> get(final Ref ref) {
		if ("local".equals(ref.getId())) {
			final Optional<String> serviceNameOptional = RefUtils.value(ref, "service");
			if (serviceNameOptional.isPresent()) {
				final String serviceName = serviceNameOptional.get();
				final Service service = services.get(serviceName);
				if (service != null) {
					return Optional.<TransportClient>of(new LocalTransportClient(ref, service));
				}
			}
		}
		return Optional.absent();
	}

	public void addService(final String id, final Service service) {
		services.put(id, service);
	}

}
