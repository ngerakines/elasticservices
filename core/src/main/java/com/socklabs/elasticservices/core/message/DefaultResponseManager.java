package com.socklabs.elasticservices.core.message;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.Gauge;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.collection.Pair;
import com.socklabs.elasticservices.core.service.DefaultMessageController;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.servo.ext.Gauges;
import com.socklabs.servo.ext.MapSizeCallable;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ngerakines on 1/2/14.
 */
public class DefaultResponseManager implements ResponseManager {

	private final ServiceProto.ServiceRef serviceRef;
	private final ServiceRegistry serviceRegistry;

	private final ConcurrentMap<Integer, Pair<SettableFuture<Message>, DateTime>> resultsFutures;

	public DefaultResponseManager(final ServiceProto.ServiceRef serviceRef, final ServiceRegistry serviceRegistry) {
		this.serviceRef = serviceRef;
		this.serviceRegistry = serviceRegistry;
		this.resultsFutures = Maps.newConcurrentMap();

		final String waitingResultsGaugeId = MessageUtils.serviceRefToString(serviceRef) + ".waitingResults";
		final Gauge<Integer> resultsFuturesGauge =
				Gauges.gauge(waitingResultsGaugeId, new MapSizeCallable(resultsFutures));
		DefaultMonitorRegistry.getInstance().register(resultsFuturesGauge);
	}

	@Override
	public AbstractFuture<Message> sendAndReceive(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass,
			final Optional<Expiration> expirationOptional) {
		return sendAndReceive(destination, message, messageClass, expirationOptional, Optional.<String> absent());
	}

	@Override
	public AbstractFuture<Message> sendAndReceive(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass,
			final Optional<Expiration> expirationOptional,
			final Optional<String> methodOptional) {
		final SettableFuture<Message> resultsFuture = SettableFuture.create();
		final byte[] messageId = MessageUtils.randomMessageId(24);
		final MessageController controller =
				new DefaultMessageController(
						serviceRef,
						destination,
						ContentTypes.fromClass(messageClass),
						Optional.of(messageId),
						Optional.<byte[]> absent(),
						expirationOptional.isPresent() ? Optional.of(expirationOptional.get().getExpiration()) : Optional
								.<DateTime> absent(),
						methodOptional);
		resultsFutures.putIfAbsent(Arrays.hashCode(messageId), new Pair<>(resultsFuture, DateTime.now()));
		serviceRegistry.sendMessage(controller, message);
		return resultsFuture;
	}

	@Override
	public SettableFuture<Message> sendAndReceive(
			final MessageController messageController,
			final AbstractMessage message) {
		final SettableFuture<Message> resultsFuture = SettableFuture.create();
		final Optional<byte[]> messageIdOptional = messageController.getMessageId();
		Preconditions.checkArgument(messageIdOptional.isPresent());
		final byte[] messageId = messageIdOptional.get();
		resultsFutures.putIfAbsent(Arrays.hashCode(messageId), new Pair<>(resultsFuture, DateTime.now()));
		serviceRegistry.sendMessage(messageController, message);
		return resultsFuture;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		final Optional<byte[]> messageId = controller.getCorrelationId();
		if (messageId.isPresent()) {
			final Pair<SettableFuture<Message>, DateTime> resultsFuturePair =
					resultsFutures.get(Arrays.hashCode(messageId.get()));
			if (resultsFuturePair != null) {
				final SettableFuture<Message> resultsFuture = resultsFuturePair.getA();
				if (message instanceof ServiceProto.EncodedError) {
					final ServiceProto.EncodedError encodedError = (ServiceProto.EncodedError) message;
					resultsFuture.setException(new Exception(encodedError.getCode()));
				} else {
					resultsFuture.set(message);
				}
				resultsFutures.remove(Arrays.hashCode(messageId.get()));
			}
		}
	}

	@Override
	public void clear(final DateTime clearPoint) {
		Iterator<Map.Entry<Integer, Pair<SettableFuture<Message>, DateTime>>> iterator =
				resultsFutures.entrySet().iterator();
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
