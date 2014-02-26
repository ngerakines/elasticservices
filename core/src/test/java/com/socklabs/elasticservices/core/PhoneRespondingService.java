package com.socklabs.elasticservices.core;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by ngerakines on 2/26/14.
 */
public interface PhoneRespondingService {

	/* Place a call to a foo, getting back bar. */
	ListenableFuture<TestProto.Bar> call(final TestProto.Foo foo);

}
