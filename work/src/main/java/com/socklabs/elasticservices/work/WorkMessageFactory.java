package com.socklabs.elasticservices.work;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.message.AbstractMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by ngerakines on 12/16/13.
 */
public class WorkMessageFactory extends AbstractMessageFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkMessageFactory.class);
	private static final Map<String, Message> PROTOTYPES = Maps.newHashMap();

	static {
		PROTOTYPES.put(
				WorkServiceProto.ListRequest.class.getName(),
				WorkServiceProto.ListRequest.getDefaultInstance());
		PROTOTYPES.put(
				WorkServiceProto.ListResponse.class.getName(),
				WorkServiceProto.ListResponse.getDefaultInstance());
	}

	public WorkMessageFactory() {
		super(Lists.newArrayList(PROTOTYPES.keySet()));
	}

	@Override
	protected Optional<Message> getPrototype(final String messageClass) {
		final Message prototype = PROTOTYPES.get(messageClass);
		if (prototype != null) {
			return Optional.of(prototype);
		}
		LOGGER.debug("Not prototype match for {}.", messageClass);
		return Optional.absent();
	}

}
