package com.socklabs.elasticservices.core.edge;

import com.socklabs.elasticservices.core.work.AbstractWork;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.concurrent.TimeUnit;

public class StaleEdgeFutureRemoverWork extends AbstractWork {

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
