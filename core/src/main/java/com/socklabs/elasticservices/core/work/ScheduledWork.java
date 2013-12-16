package com.socklabs.elasticservices.core.work;

import com.google.common.base.Optional;
import org.joda.time.DateTime;

/**
 * Created by ngerakines on 12/16/13.
 */
public interface ScheduledWork extends Work {

	Optional<DateTime> getLastRun();

	boolean isEnabled();

	public enum SchedulePhase implements Phase {
		CREATED, WAITING_TO_RUN, STARTING, STARTED, STOPPING, STOPPED
	}

}
