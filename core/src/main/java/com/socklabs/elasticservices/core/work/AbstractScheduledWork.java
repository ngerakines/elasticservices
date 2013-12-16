package com.socklabs.elasticservices.core.work;

import com.google.common.base.Optional;
import com.socklabs.feature.ToggleFeature;
import org.joda.time.DateTime;

/**
 * Created by ngerakines on 12/16/13.
 */
public abstract class AbstractScheduledWork extends AbstractWork implements ScheduledWork {

	private final String id;
	private final ToggleFeature toggleFeature;
	private DateTime lastRun;

	public AbstractScheduledWork(final String id, final ToggleFeature toggleFeature) {
		super(SchedulePhase.CREATED);
		this.id = id;
		this.toggleFeature = toggleFeature;
		this.lastRun = null;
	}

	@Override
	public Optional<DateTime> getLastRun() {
		return Optional.fromNullable(lastRun);
	}

	@Override
	public boolean isEnabled() {
		return toggleFeature.isEnabled();
	}

	@Override
	public String getId() {
		return id;
	}

}
