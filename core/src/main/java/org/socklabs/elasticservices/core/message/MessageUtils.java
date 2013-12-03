package org.socklabs.elasticservices.core.message;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.ServiceProto;

import java.security.SecureRandom;
import java.util.Random;

public class MessageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);

    private static final HashFunction SHA256 = Hashing.sha256();

    private static final BaseEncoding B16 = BaseEncoding.base16();

    private static final Joiner SEMI_COLON_JOINER = Joiner.on(":");

    private static final Random RANDOM = new SecureRandom();

    public static Optional<Message> fromJson(final Message prototype, final String json) {
        final Message.Builder builder = prototype.newBuilderForType();
        try {
            JsonFormat.merge(json, builder);
            return Optional.of(builder.build());
        } catch (final JsonFormat.ParseException e) {
            LOGGER.error("Could not create protobuf message from json structure.", e);
        }
        return Optional.absent();
    }

    public static String buildMessageId(final Message message) {
        final byte[] hashedBytes = SHA256.hashBytes(message.toByteArray()).asBytes();
        return B16.encode(hashedBytes);
    }

    public static byte[] randomMessageId(int length) {
        final byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static String componentRefToString(final ServiceProto.ComponentRef componentRef) {
        return SEMI_COLON_JOINER.join(componentRef.getSite(), componentRef.getCluster(), componentRef.getComponentId());
    }

}
