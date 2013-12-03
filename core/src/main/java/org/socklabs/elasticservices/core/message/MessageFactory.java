package org.socklabs.elasticservices.core.message;

import com.google.common.base.Optional;
import com.google.protobuf.Message;
import org.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

public interface MessageFactory {

    List<String> supportedMessagePackages();

    Optional<Message> get(final MessageController controller, final byte[] rawMessage);

}
