package com.socklabs.elasticservices.core.work;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A base class for defining and implementing work. Here, a standard Phase is
 * also made available for common use cases.
 */
public abstract class AbstractWork implements Work {

	private final AtomicReference<Phase> phase;
	private final AtomicBoolean shutdown = new AtomicBoolean(false);

	public AbstractWork() {
		this.phase = new AtomicReference<Phase>(StandardPhase.CREATED);
	}

	@Override
	public void stop() {
		shutdown.set(true);
	}

	@Override
	public Phase getPhase() {
		return phase.get();
	}

	protected void setPhase(final Phase phase) {
		this.phase.set(phase);
	}

	protected boolean isShuttingDown() {
		return shutdown.get();
	}

	public enum StandardPhase implements Phase {
		CREATED, STARTING, STARTED, STOPPING, STOPPED
	}

}
