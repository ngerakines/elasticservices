package com.socklabs.servo.ext;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.Monitor;

public class MonitorRemovalListener<K, V extends Monitor<?>> implements RemovalListener<K, V> {

	@Override
	public void onRemoval(final RemovalNotification<K, V> notification) {
		DefaultMonitorRegistry.getInstance().unregister(notification.getValue());
	}

}
