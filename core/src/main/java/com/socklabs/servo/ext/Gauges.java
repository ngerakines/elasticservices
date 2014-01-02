package com.socklabs.servo.ext;

import com.netflix.servo.monitor.BasicGauge;
import com.netflix.servo.monitor.Gauge;
import com.netflix.servo.monitor.MonitorConfig;

import java.util.concurrent.Callable;

/**
 * Created by ngerakines on 1/2/14.
 */
public class Gauges {

	public static <T extends Number> Gauge<T> gauge(final String id, final Callable<T> callable) {
		return new BasicGauge<>(MonitorConfig.builder(id).build(), callable);
	}

}
