package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.misc.Ref;

/**
 * Created by ngerakines on 12/7/13.
 */
public interface TransportClientFactory {
	Optional<TransportClient> get(Ref ref);
}
