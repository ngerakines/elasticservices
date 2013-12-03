package org.socklabs.elasticservices.core.edge;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.ContentTypes;
import org.socklabs.elasticservices.core.message.MessageUtils;
import org.socklabs.elasticservices.core.service.DefaultMessageController;
import org.socklabs.elasticservices.core.service.MessageController;
import org.socklabs.elasticservices.core.service.ServiceRegistry;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class DefaultEdgeManager implements EdgeManager {

    private final ServiceProto.ServiceRef serviceRef;
    private final ServiceRegistry serviceRegistry;

    private final ConcurrentMap<Integer, SettableFuture<Message>> resultsFutures;

    public DefaultEdgeManager(final ServiceProto.ServiceRef serviceRef, final ServiceRegistry serviceRegistry){
        this.serviceRef = serviceRef;
        this.serviceRegistry = serviceRegistry;
        this.resultsFutures = Maps.newConcurrentMap();
    }

    @Override
    public Future<Message> execute(final ServiceProto.ServiceRef destination, final AbstractMessage message, final Class messageClass) {
        final SettableFuture<Message> resultsFuture = SettableFuture.create();
        final byte[] messageId = MessageUtils.randomMessageId(24);
        final MessageController controller = new DefaultMessageController(
                serviceRef,
                destination,
                ContentTypes.fromJsonClass(messageClass),
                Optional.of(messageId),
                Optional.<byte[]>absent());
        resultsFutures.putIfAbsent(Arrays.hashCode(messageId), resultsFuture);
        serviceRegistry.sendMessage(controller, message);
        return resultsFuture;
    }

    @Override
    public void handleMessage(final MessageController controller, final Message message) {
        final Optional<byte[]> messageId = controller.getCorrelationId();
        if (messageId.isPresent()) {
            final SettableFuture<Message> resultsFuture = resultsFutures.get(Arrays.hashCode(messageId.get()));
            if (resultsFuture != null)
            {
                resultsFuture.set(message);
                resultsFutures.remove(Arrays.hashCode(messageId.get()));
            }
        }
    }

}
