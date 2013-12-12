package com.socklabs.elasticservices.core.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.socklabs.elasticservices.core.ServiceProto;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

public abstract class AbstractService implements Service {

	private final ServiceProto.ServiceRef serviceRef;
	private final Set<Integer> serviceFlags;

	protected AbstractService(final ServiceProto.ServiceRef serviceRef) {
		this.serviceRef = serviceRef;
		this.serviceFlags = Sets.newHashSet();
	}

	@Override
	public ServiceProto.ServiceRef getServiceRef() {
		return serviceRef;
	}

	@Override
	public List<Integer> getFlags() {
		return ImmutableList.copyOf(serviceFlags);
	}

	@Override
	public void setFlag(final int flag) {
		serviceFlags.add(flag);
	}

	@Override
	public void removeFlag(final int flag) {
		serviceFlags.remove(flag);
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
