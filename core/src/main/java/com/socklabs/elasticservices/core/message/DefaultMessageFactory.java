package com.socklabs.elasticservices.core.message;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by ngerakines on 1/2/14.
 */
public class DefaultMessageFactory extends AbstractMessageFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageFactory.class);

	private final Map<String, Message> prototypes;

	public DefaultMessageFactory(final List<Message> prototypes) {
		super(ImmutableList.copyOf(Lists.transform(prototypes, new PrototypeNameFunction())));
		this.prototypes = Maps.uniqueIndex(prototypes, new NameFromMessageFunction());
	}

	@Override
	protected Optional<Message> getPrototype(final String messageClass) {
		final Message prototype = prototypes.get(messageClass);
		if (prototype != null) {
			return Optional.of(prototype);
		}
		LOGGER.debug("Not prototype match for {}.", messageClass);
		return Optional.absent();
	}

	private static class PrototypeNameFunction implements Function<Message, String> {

		@Override
		public String apply(final Message message) {
			return message.getClass().getName();
		}

	}

	private static class NameFromMessageFunction implements Function<Message, String> {

		@Override
		public String apply(final Message message) {
			return message.getClass().getName();
		}

	}

}
