package org.socklabs.elasticservices.core.transport;

public interface TransportFactory {

	Transport get(final String transportRef);

}
