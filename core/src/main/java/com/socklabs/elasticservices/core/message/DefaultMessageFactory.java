package com.socklabs.elasticservices.core.message;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by ngerakines on 1/2/14.
 */
public class DefaultMessageFactory extends AbstractMessageFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageFactory.class);

	private final Map<String, Message> prototypes;

	public DefaultMessageFactory(final Map<String, Message> prototypes) {
		super(Lists.newArrayList(prototypes.keySet()));
		this.prototypes = prototypes;
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

}
