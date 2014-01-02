package com.socklabs.elasticservices.core.message;

import com.socklabs.elasticservices.core.work.AbstractWork;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.concurrent.TimeUnit;

public class ResponseRemovalWork extends AbstractWork {

	private final String id;
	private final ResponseManager responseManager;
	private final int frequencyInSeconds;
	private final Duration duration;

	public ResponseRemovalWork(
			final String id,
			final ResponseManager responseManager,
			final int frequencyInSeconds,
			final Duration duration) {
		super();
		this.id = id;
		this.responseManager = responseManager;
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
			responseManager.clear(clearPoint);
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
