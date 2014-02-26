package com.socklabs.elasticservices.core;

import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import com.socklabs.elasticservices.core.message.ResponseManager;
import com.socklabs.elasticservices.core.transport.RespondingServiceTransportClientProxyHandler;

/**
 * Created by ngerakines on 2/26/14.
 */
public class PhoneRespondingServiceStub {

	public static PhoneRespondingService create(
			final ServiceProto.ServiceRef destination,
			final ResponseManager responseManager) {
		final AbstractInvocationHandler abstractInvocationHandler =
				new RespondingServiceTransportClientProxyHandler<PhoneRespondingService>(responseManager, destination);
		return Reflection.newProxy(PhoneRespondingService.class, abstractInvocationHandler);
	}

}
