package com.socklabs.feature;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicPropertyFactory;

/**
 * Created by ngerakines on 12/13/13.
 */
public class DefaultToggleFeature implements ToggleFeature {

	private final String id;
	private final DynamicBooleanProperty property;

	public DefaultToggleFeature(final String id) {
		this(id, true);
	}

	public DefaultToggleFeature(final String id, final boolean defaultValue) {
		this.id = id;
		this.property = DynamicPropertyFactory.getInstance()
				.getBooleanProperty(id, defaultValue);
	}

	@Override
	public boolean isEnabled() {
		return property.get();
	}

	@Override
	public String getId() {
		return id;
	}

}
