package org.socklabs.elasticservices.core.work;

/**
 * A unit of work that needs to be managed by the system.
 */
public interface Work {

	public interface Phase {

	}

	String getId();

	void run();

	void stop();

	Phase getPhase();

}
