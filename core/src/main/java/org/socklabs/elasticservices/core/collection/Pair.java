package org.socklabs.elasticservices.core.collection;

public class Pair<A, B> {

	private final A a;
	private final B b;

	public Pair(final A a, final B b) {
		this.a = a;
		this.b = b;
	}

	public static <A, B> Pair<A, B> create(final A a, final B b) {
		return new Pair<>(a, b);
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	@Override
	public int hashCode() {
		int result = a.hashCode();
		result = 31 * result + b.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Pair)) {
			return false;
		}

		final Pair pair = (Pair) o;

		if (!a.equals(pair.a)) {
			return false;
		}
		if (!b.equals(pair.b)) {
			return false;
		}

		return true;
	}
}
