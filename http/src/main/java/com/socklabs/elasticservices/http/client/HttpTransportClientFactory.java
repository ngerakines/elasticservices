package com.socklabs.elasticservices.http.client;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.transport.TransportClient;
import com.socklabs.elasticservices.core.transport.TransportClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ngerakines on 12/28/13.
 */
public class HttpTransportClientFactory implements TransportClientFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransportClientFactory.class);

	@Override
	public Optional<TransportClient> get(final Ref ref) {
		LOGGER.debug("Transport client requested for ref {}.", ref.toString());
		if ("http".equals(ref.getId())) {
			return Optional.<TransportClient>of(new HttpTransportClient(ref));
		}
		return Optional.absent();
	}

}
