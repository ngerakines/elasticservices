package com.socklabs.elasticservices.core.collection;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;
import com.socklabs.elasticservices.core.ServiceProto;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

	private CollectionUtils() {
	}

	@Nullable
	public static <K, V> V getfirst(final Multimap<K, V> collection, final K key) {
		// NKG: An alternative would be to create an immutable list and simply
		// return the first entry.
		final Collection<V> values = collection.get(key);
		final Iterator<V> iterator = values.iterator();
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	public static Optional<String> getAttributeValue(final Map<String, String> attributes, final String key) {
		if (attributes != null) {
			final String value = attributes.get(key);
			if (value != null) {
				return Optional.of(value);
			}
		}
		return Optional.absent();
	}

	public static Optional<String> firstAttributeValue(
			final List<ServiceProto.ContentType.Attribute> attributes, final String key) {
		if (attributes != null) {
			final Optional<ServiceProto.ContentType.Attribute> attributeOptional = Iterables.tryFind(
					attributes, new AttributeNamePredicate(key));
			if (attributeOptional.isPresent()) {
				final ServiceProto.ContentType.Attribute attribute = attributeOptional.get();
				if (attribute.hasValue()) {
					return Optional.of(attribute.getValue());
				}
			}
		}
		return Optional.absent();
	}

	public static List<Long> convertStringsToLongs(final List<String> values) {
		final List<Long> longs = Lists.newArrayList();
		for (final String value : values) {
			final Long convertedValue = Longs.tryParse(value);
			if (convertedValue != null) {
				longs.add(convertedValue);
			}
		}
		return longs;
	}

	private static class AttributeNamePredicate implements Predicate<ServiceProto.ContentType.Attribute> {

		private final String key;

		private AttributeNamePredicate(String key) {
			this.key = key;
		}

		@Override
		public boolean apply(@Nullable ServiceProto.ContentType.Attribute input) {
			return input != null && input.hasKey() && input.getKey().equals(key);
		}

	}

}
