package com.socklabs.elasticservices.core.transport;

import com.google.common.base.Optional;
import com.socklabs.elasticservices.core.misc.Ref;

public interface TransportFactory {

	Optional<Transport> get(final Ref transportRef);

}
