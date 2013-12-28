package com.socklabs.elasticservices.core.service;

import com.google.common.collect.Multimap;
import com.socklabs.elasticservices.core.ServiceProto;

/**
 * An object that can receive and act on component status events.
 */
public interface ServicePresenceListener {

	void updateComponentServices(
			final ServiceProto.ComponentRef componentRef,
			final Multimap<ServiceProto.ServiceRef, String> services,
			final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags);

}
