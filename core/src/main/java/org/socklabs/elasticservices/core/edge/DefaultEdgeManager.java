package org.socklabs.elasticservices.core.edge;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import org.joda.time.DateTime;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.collection.Pair;
import org.socklabs.elasticservices.core.message.ContentTypes;
import org.socklabs.elasticservices.core.message.MessageUtils;
import org.socklabs.elasticservices.core.service.DefaultMessageController;
import org.socklabs.elasticservices.core.service.MessageController;
import org.socklabs.elasticservices.core.service.ServiceRegistry;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class DefaultEdgeManager implements EdgeManager {

	private final ServiceProto.ServiceRef serviceRef;
	private final ServiceRegistry serviceRegistry;

	private final ConcurrentMap<Integer, Pair<SettableFuture<Message>, DateTime>> resultsFutures;

	public DefaultEdgeManager(final ServiceProto.ServiceRef serviceRef, final ServiceRegistry serviceRegistry) {
		this.serviceRef = serviceRef;
		this.serviceRegistry = serviceRegistry;
		this.resultsFutures = Maps.newConcurrentMap();
	}

	@Override
	public Future<Message> execute(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass) {
		final SettableFuture<Message> resultsFuture = SettableFuture.create();
		final byte[] messageId = MessageUtils.randomMessageId(24);
		final MessageController controller = new DefaultMessageController(
				serviceRef,
				destination,
				ContentTypes.fromJsonClass(messageClass),
				Optional.of(messageId),
				Optional.<byte[]>absent());
		resultsFutures.putIfAbsent(Arrays.hashCode(messageId), new Pair<>(resultsFuture, DateTime.now()));
		serviceRegistry.sendMessage(controller, message);
		return resultsFuture;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		final Optional<byte[]> messageId = controller.getCorrelationId();
		if (messageId.isPresent()) {
			final Pair<SettableFuture<Message>, DateTime> resultsFuturePair = resultsFutures.get(
					Arrays.hashCode(
							messageId.get()));
			if (resultsFuturePair != null) {
				final SettableFuture<Message> resultsFuture = resultsFuturePair.getA();
				resultsFuture.set(message);
				resultsFutures.remove(Arrays.hashCode(messageId.get()));
			}
		}
	}

	@Override
	public void clear(final DateTime clearPoint) {
		Iterator<Map.Entry<Integer, Pair<SettableFuture<Message>, DateTime>>> iterator = resultsFutures.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			final Map.Entry<Integer, Pair<SettableFuture<Message>, DateTime>> entry = iterator.next();
			final Pair<SettableFuture<Message>, DateTime> resultsPair = entry.getValue();
			final DateTime createdAt = resultsPair.getB();
			if (createdAt.isBefore(clearPoint)) {
				iterator.remove();
			}
		}
	}

}
