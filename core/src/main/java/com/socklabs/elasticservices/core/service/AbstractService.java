package com.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.ServiceProto;
import org.joda.time.DateTime;

public abstract class AbstractService implements Service {

	private final ServiceProto.ServiceRef serviceRef;

	protected AbstractService(final ServiceProto.ServiceRef serviceRef) {
		this.serviceRef = serviceRef;
	}

	@Override
	public ServiceProto.ServiceRef getServiceRef() {
		return serviceRef;
	}

	protected boolean messageHasExpired(final MessageController controller) {
		final Optional<DateTime> expiresOptional = controller.getExpires();
		if (expiresOptional.isPresent()) {
			final DateTime expires = expiresOptional.get();
			if (DateTime.now().isAfter(expires)) {
				return true;
			}
		}
		return false;
	}

}
