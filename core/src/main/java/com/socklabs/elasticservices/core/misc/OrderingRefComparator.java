package com.socklabs.elasticservices.core.misc;

import com.google.common.base.Optional;
import com.google.common.primitives.Ints;
import com.socklabs.elasticservices.core.collection.Pair;

import java.util.Comparator;

/**
 * Created by ngerakines on 12/22/13.
 */
public class OrderingRefComparator implements Comparator<Ref> {

	@Override
	public int compare(final Ref o1, final Ref o2) {
		final Integer o1v = getOrder(o1);
		final Integer o2v = getOrder(o2);
		if (o1v.equals(o2v)) {
			return o1.getId().compareTo(o2.getId());
		}
		return o1v.compareTo(o2v);
	}

	private Integer getOrder(final Ref ref) {
		Optional<Pair<String,Optional<String>>> valueOptional = ref.getValue("order");
		if (valueOptional.isPresent()) {
			final Pair<String, Optional<String>> pair = valueOptional.get();
			if (pair.getB().isPresent()) {
				final String rawValue = pair.getB().get();
				final Integer value = Ints.tryParse(rawValue);
				if (value != null) {
					return value;
				}
			}
		}
		return 0;
	}

}
