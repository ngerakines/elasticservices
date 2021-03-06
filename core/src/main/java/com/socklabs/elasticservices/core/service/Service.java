package com.socklabs.elasticservices.core.service;

import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.MessageFactory;

import java.util.List;

/**
 * An object that can receive messages.
 */
public interface Service {

	/**
	 * Returns a service reference for the service.
	 */
	ServiceProto.ServiceRef getServiceRef();

	/**
	 * Returns a list of message factories used by the service to compose messages.
	 */
	List<MessageFactory> getMessageFactories();

	/**
	 * The callback used to deliver messages to the service.
	 */
	void handleMessage(final MessageController controller, final Message message);

	/**
	 * Returns a list of flags that can be used to describe the state of the service.
	 */
	List<Integer> getFlags();

	void setFlag(final int flag);

	void removeFlag(final int flag);

}
