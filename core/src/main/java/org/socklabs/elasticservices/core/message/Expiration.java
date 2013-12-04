package org.socklabs.elasticservices.core.message;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

public class Expiration {
	private final DateTime expiration;

	public Expiration(final DateTime expiration) {
		this.expiration = expiration;
	}

	public static Expiration fromNow(final long value, final TimeUnit timeUnit) {
		final DateTime expiration = DateTime.now().plusMillis((int) timeUnit.toMillis(value));
		return new Expiration(expiration);
	}

	public DateTime getExpiration() {
		return expiration;
	}
}
