package com.socklabs.servo.ext;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.BasicCounter;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.MonitorConfig;

public class CounterCacheLoader<K> extends CacheLoader<K, Counter> {

	private final Optional<String> prefix;
	private final String separator;

	public CounterCacheLoader() {
		this(Optional.<String>absent());
	}

	public CounterCacheLoader(final Optional<String> prefix) {
		this(prefix, ".");
	}

	public CounterCacheLoader(final Optional<String> prefix, final String separator) {
		this.prefix = prefix;
		this.separator = separator;
	}

	@Override
	public Counter load(final K key) throws Exception {
		final Counter c = new BasicCounter(MonitorConfig.builder(buildId(key)).build());
		DefaultMonitorRegistry.getInstance().register(c);
		return c;
	}

	private String buildId(final K key) {
		if (prefix.isPresent()) {
			final String prefixValue = prefix.get();
			return prefixValue + separator + key.toString();
		}
		return key.toString();
	}

}
