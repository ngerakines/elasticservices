package com.socklabs.elasticservices.core.misc;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.socklabs.elasticservices.core.collection.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ngerakines on 12/7/13.
 */
public class RefTest {

	@Test
	public void create() {
		final Ref fooRef = new Ref("foo");
		Assert.assertEquals(fooRef.getId(), "foo");
		Assert.assertEquals(fooRef.getValues().size(), 0);

		final Ref barRef = new Ref("bar", ImmutableList.of(new Pair<>("bar1", Optional.<String>absent())));
		Assert.assertEquals(barRef.getId(), "bar");
		Assert.assertEquals(barRef.getValues().size(), 1);
		Assert.assertEquals(barRef.getValues().get(0).getA(), "bar1");
	}

}
