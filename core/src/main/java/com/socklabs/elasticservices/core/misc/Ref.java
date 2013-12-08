package com.socklabs.elasticservices.core.misc;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.socklabs.elasticservices.core.collection.Pair;

import java.util.List;

public class Ref {

	private static final Splitter GROUP_SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();
	private static final Splitter KV_SPLITTER = Splitter.on("=").omitEmptyStrings().trimResults().limit(2);

	private final String id;
	private final ImmutableList<Pair<String, Optional<String>>> values;

	public Ref(final String id) {
		this(id, Lists.<Pair<String, Optional<String>>>newArrayList());
	}

	public Ref(
			final String id,
			final List<Pair<String, Optional<String>>> values) {
		this.id = id;
		this.values = ImmutableList.copyOf(values);
	}

	public String getId() {
		return id;
	}

	public List<Pair<String, Optional<String>>> getValues() {
		return values;
	}

	public Optional<Pair<String, Optional<String>>> getValue(final String key) {
		for (final Pair<String, Optional<String>> value : values) {
			if (key.equals(value.getA())) {
				return Optional.of(value);
			}
		}
		return Optional.absent();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(id);
		if (values.size() > 0) {
			for (final Pair<String, Optional<String>> pair : values) {
				sb.append(";");
				sb.append(pair.getA());
				final Optional<String> valueOptional = pair.getB();
				if (valueOptional.isPresent()) {
					sb.append("=");
					sb.append(valueOptional.get());
				}
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		final Ref ref = (Ref) o;

		if (!id.equals(ref.id)) { return false; }
		if (!values.equals(ref.values)) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + values.hashCode();
		return result;
	}

	public static Builder builderFromUri(final String uri) {
		final List<String> groups = ImmutableList.copyOf(GROUP_SPLITTER.split(uri));
		Preconditions.checkArgument(groups.size() > 0, "Invalid URI.");
		final String id = groups.get(0);
		final Builder builder = new Builder(id);
		for (int i = 1; i < groups.size(); i++) {
			final String group = groups.get(i);
			final List<String> groupParts = ImmutableList.copyOf(KV_SPLITTER.split(group));
			Preconditions.checkArgument(groupParts.size() > 0, "Invalid URI.");
			if (groupParts.size() == 1) {
				builder.addValue(groupParts.get(0));
			} else if (groupParts.size() == 2) {
				builder.addValue(groupParts.get(0), groupParts.get(1));
			}
		}
		return builder;
	}

	public static Builder builder(final String id) {
		return new Builder(id);
	}

	public static class Builder {
		private final String id;
		private final List<Pair<String, Optional<String>>> values;

		private Builder(final String id) {
			this.id = id;
			this.values = Lists.newArrayList();
		}

		public Builder addValue(final String key) {
			values.add(new Pair<>(key, Optional.<String>absent()));
			return this;
		}

		public Builder addValue(final String key, final String value) {
			values.add(new Pair<>(key, Optional.of(value)));
			return this;
		}

		public Ref build() {
			return new Ref(id, values);
		}

	}

}
