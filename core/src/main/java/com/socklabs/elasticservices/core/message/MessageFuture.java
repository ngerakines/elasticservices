package com.socklabs.elasticservices.core.message;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.protobuf.Message;

import javax.annotation.Nullable;

/**
 * Created by ngerakines on 2/19/14.
 */
public class MessageFuture<V extends Message> extends AbstractFuture<V> {

	@Override
	public boolean set(@Nullable final V value) {
		return super.set(value);
	}

	@Override
	public boolean setException(final Throwable throwable) {
		return super.setException(throwable);
	}

}
