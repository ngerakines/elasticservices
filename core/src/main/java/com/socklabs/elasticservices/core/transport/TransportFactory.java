package com.socklabs.elasticservices.core.transport;

import com.socklabs.elasticservices.core.misc.Ref;

public interface TransportFactory {

	Transport get(final Ref transportRef);

}
