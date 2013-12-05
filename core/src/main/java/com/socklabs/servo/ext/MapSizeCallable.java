package com.socklabs.servo.ext;

import java.util.Map;
import java.util.concurrent.Callable;

public class MapSizeCallable implements Callable<Integer> {

	private final Map<?, ?> map;

	public MapSizeCallable(final Map<?, ?> map) {
		this.map = map;
	}

	@Override
	public Integer call() throws Exception {
		return map.size();
	}

}
