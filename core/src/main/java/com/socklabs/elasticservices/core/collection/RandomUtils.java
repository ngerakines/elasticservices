package com.socklabs.elasticservices.core.collection;

import java.security.SecureRandom;
import java.util.List;

public class RandomUtils {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

	public static byte[] randomBytes(final int size) {
		final byte[] bytes = new byte[size];
		RANDOM.nextBytes(bytes);
		return bytes;
	}

	public static String randomString(final int length) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}

	public static <T> T randomFromListOf(List<T> list) {
		final int size = list.size();
		return list.get(RANDOM.nextInt(size));
	}

	public static boolean chance(int chance) {
		final int challenge = randomInt(100);
		return challenge < chance;
	}

	public static int randomInt(final int max) {
		return randomInt(1, max);
	}

	public static int randomInt(final int min, final int max) {
		return RANDOM.nextInt(max) + min;
	}

}
