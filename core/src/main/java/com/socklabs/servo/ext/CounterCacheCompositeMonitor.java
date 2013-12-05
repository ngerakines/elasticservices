package com.socklabs.servo.ext;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.netflix.servo.monitor.CompositeMonitor;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.Monitor;
import com.netflix.servo.monitor.MonitorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CounterCacheCompositeMonitor<K> implements CompositeMonitor, KeyIncrementable<K> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CounterCacheCompositeMonitor.class);

	private final LoadingCache<K, Counter> counters;
	private final String id;

	public CounterCacheCompositeMonitor(final String id) {
		this.id = id;

		final CacheBuilder<K, Counter> cacheBuilder = CacheBuilder.newBuilder()
				.removalListener(new MonitorRemovalListener<K, Counter>());
		this.counters = cacheBuilder.build(new CounterCacheLoader<K>());
	}

	@Override
	public List<Monitor<?>> getMonitors() {
		final List<Monitor<?>> monitors = Lists.newArrayList();
		monitors.addAll(counters.asMap().values());
		return monitors;
	}

	@Override
	public Object getValue() {
		return counters.size();
	}

	@Override public MonitorConfig getConfig() {
		return MonitorConfig.builder(id).build();
	}

	public void incr(final K key) {
		try {
			counters.get(key).increment();
		} catch (final UncheckedExecutionException | ExecutionException | ExecutionError e) {
			LOGGER.error("Error incrementing counter.", e);
		}
	}

}
