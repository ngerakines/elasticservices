package org.socklabs.elasticservices.core.message;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.service.MessageController;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractMessageFactory implements MessageFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageFactory.class);

	private final List<String> supportedMessagePackages;

	public AbstractMessageFactory(final List<String> supportedMessagePackages) {
		this.supportedMessagePackages = ImmutableList.copyOf(supportedMessagePackages);
	}

	@Override
	public List<String> supportedMessagePackages() {
		return supportedMessagePackages;
	}

	@Override
	public Optional<Message> get(final MessageController controller, final byte[] rawMessage) {
		final ServiceProto.ContentType contentType = controller.getContentType();
		if (contentType == null) {
			throw new RuntimeException("Could not determine type of message.");
		}
		final Optional<String> messageClassOptional = firstAttributeValue(contentType.getAttributeList(), "class");
		if (!messageClassOptional.isPresent()) {
			throw new RuntimeException("Could not determine message class of message.");
		}
		final Optional<Message> messagePrototypeOptional = getPrototype(messageClassOptional.get());
		if (!messagePrototypeOptional.isPresent()) {
			throw new RuntimeException("No prototype available for class.");
		}
		final Message messagePrototype = messagePrototypeOptional.get();
		if (contentType.getValue().equals(ContentTypes.CONTENT_TYPE_JSON)) {
			final Message.Builder builder = messagePrototype.newBuilderForType();
			final String json = new String(rawMessage);
			try {
				JsonFormat.merge(json, builder);
				return Optional.of(builder.build());
			} catch (final JsonFormat.ParseException e) {
				LOGGER.error("Could not create protobuf message from json structure.", e);
			}
		}
		if (contentType.getValue().equals(ContentTypes.CONTENT_TYPE_PB)) {
			final Message.Builder builder = messagePrototype.newBuilderForType();
			try {
				builder.mergeFrom(rawMessage);
				return Optional.of(builder.build());
			} catch (final InvalidProtocolBufferException e) {
				LOGGER.error("Could not create protobuf message from merged raw bytes.", e);
			}
		}
		return Optional.absent();
	}

	protected abstract Optional<Message> getPrototype(final String messageClass);

	private Optional<String> firstAttributeValue(
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
