package com.socklabs.servo.ext;

import java.util.Collection;
import java.util.concurrent.Callable;

public class CollectionSizeCallable implements Callable<Integer> {

	private final Collection<?> collection;

	public CollectionSizeCallable(final Collection<?> collection) {
		this.collection = collection;
	}

	@Override public Integer call() throws Exception {
		return collection.size();
	}

}
