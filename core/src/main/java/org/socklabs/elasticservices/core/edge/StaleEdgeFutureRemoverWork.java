package org.socklabs.elasticservices.core.edge;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.work.AbstractWork;

import java.util.concurrent.TimeUnit;

public class StaleEdgeFutureRemoverWork extends AbstractWork {

	private static final Logger LOGGER = LoggerFactory.getLogger(StaleEdgeFutureRemoverWork.class);

	private final String id;
	private final EdgeManager edgeManager;
	private final int frequencyInSeconds;
	private final Duration duration;

	public StaleEdgeFutureRemoverWork(
			final String id,
			final EdgeManager edgeManager,
			final int frequencyInSeconds,
			final Duration duration) {
		super();
		this.id = id;
		this.edgeManager = edgeManager;
		this.frequencyInSeconds = frequencyInSeconds;
		this.duration = duration;
		setPhase(StandardPhase.STARTING);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void run() {
		setPhase(StandardPhase.STARTED);
		while (!isShuttingDown()) {
			final DateTime clearPoint = DateTime.now().minus(duration);
			edgeManager.clear(clearPoint);
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(frequencyInSeconds));
			} catch (final InterruptedException e) {
				throw new RuntimeException("Sleep interupted.", e);
			}
		}
		setPhase(StandardPhase.STOPPING);
		setPhase(StandardPhase.STOPPED);
	}

}
