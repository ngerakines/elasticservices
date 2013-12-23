package com.socklabs.elasticservices.core.service;

import com.google.common.collect.Multimap;
import com.socklabs.elasticservices.core.ServiceProto;

/**
 * Created by ngerakines on 12/22/13.
 */
public interface ServicePresenceListener {

	void updateComponentServices(
			final ServiceProto.ComponentRef componentRef,
			final Multimap<ServiceProto.ServiceRef, String> services,
			final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags);

}
