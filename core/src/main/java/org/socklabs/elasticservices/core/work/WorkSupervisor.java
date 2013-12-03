package org.socklabs.elasticservices.core.work;

/**
 * A manager and controller of work.
 */
public interface WorkSupervisor {

    /**
     * Starts any work given to the supervisor.
     */
    void start();

    /**
     * Attempts to stop any work given to the supervisor.
     */
    void stop();

    /**
     * Add a unit of work to the supervisor.
     */
    void addWork(final Work work);

}
