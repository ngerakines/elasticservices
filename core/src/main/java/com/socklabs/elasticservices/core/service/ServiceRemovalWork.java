package com.socklabs.elasticservices.core.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.collection.Pair;
import com.socklabs.elasticservices.core.work.AbstractWork;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ngerakines on 12/23/13.
 */
public class ServiceRemovalWork extends AbstractWork implements ServicePresenceListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRemovalWork.class);

	private final ServiceRegistry serviceRegistry;
	private final Duration duration;

	private final Map<Pair<ServiceProto.ServiceRef, String>, DateTime> lastReference;

	public ServiceRemovalWork(final ServiceRegistry serviceRegistry, final Duration duration) {
		this.serviceRegistry = serviceRegistry;
		this.duration = duration;

		this.lastReference = Maps.newHashMap();

		setPhase(StandardPhase.CREATED);
	}

	@Override
	public void updateComponentServices(
			final ServiceProto.ComponentRef componentRef,
			final Multimap<ServiceProto.ServiceRef, String> services,
			final Multimap<ServiceProto.ServiceRef, Integer> serviceFlags) {

		for (final Map.Entry<ServiceProto.ServiceRef, String> entry : services.entries()) {
			lastReference.put(new Pair<>(entry.getKey(), entry.getValue()), DateTime.now());
		}

	}

	@Override
	public String getId() {
		return "system:service:remover";
	}

	@Override
	public void run() {
		setPhase(StandardPhase.STARTING);
		setPhase(StandardPhase.STARTED);
		while (!isShuttingDown()) {

			final DateTime now = DateTime.now();
			for (final Map.Entry<Pair<ServiceProto.ServiceRef, String>, DateTime> entry : lastReference.entrySet()) {
				if (now.isAfter(entry.getValue().plus(duration))) {
					final Pair<ServiceProto.ServiceRef, String> pair = entry.getKey();
					serviceRegistry.deregisterService(pair.getA());
				}
			}
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(1));
			} catch (final InterruptedException e) {
				LOGGER.error("Exception caught while waiting for next run cycle.", e);
			}
		}
		setPhase(StandardPhase.STOPPING);
		setPhase(StandardPhase.STOPPED);
	}

}
