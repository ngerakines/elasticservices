package com.socklabs.elasticservices.core.edge;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.BasicGauge;
import com.netflix.servo.monitor.Gauge;
import com.netflix.servo.monitor.MonitorConfig;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.collection.Pair;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.Expiration;
import com.socklabs.elasticservices.core.message.MessageUtils;
import com.socklabs.elasticservices.core.service.DefaultMessageController;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.servo.ext.MapSizeCallable;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class DefaultEdgeManager implements EdgeManager {

	private final ServiceProto.ServiceRef serviceRef;
	private final ServiceRegistry serviceRegistry;

	private final ConcurrentMap<Integer, Pair<SettableFuture<Message>, DateTime>> resultsFutures;

	public DefaultEdgeManager(
			final ServiceProto.ServiceRef serviceRef,
			final ServiceRegistry serviceRegistry) {
		this.serviceRef = serviceRef;
		this.serviceRegistry = serviceRegistry;
		this.resultsFutures = Maps.newConcurrentMap();

		final String waitingResultsGaugeId = MessageUtils.serviceRefToString(serviceRef) + ".waitingResults";
		final Gauge resultsFuturesGauge = new BasicGauge<>(
				MonitorConfig.builder(waitingResultsGaugeId).build(),
				new MapSizeCallable(resultsFutures));
		DefaultMonitorRegistry.getInstance().register(resultsFuturesGauge);
	}

	@Override
	public Future<Message> sendAndReceive(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass,
			final Optional<Expiration> expirationOptional) {
		final SettableFuture<Message> resultsFuture = SettableFuture.create();
		final byte[] messageId = MessageUtils.randomMessageId(24);
		final MessageController controller = new DefaultMessageController(
				serviceRef,
				destination,
				ContentTypes.fromJsonClass(messageClass),
				Optional.of(messageId),
				Optional.<byte[]>absent(),
				expirationOptional.isPresent() ?
						Optional.of(expirationOptional.get().getExpiration()) : Optional.<DateTime>absent());
		resultsFutures.putIfAbsent(Arrays.hashCode(messageId), new Pair<>(resultsFuture, DateTime.now()));
		serviceRegistry.sendMessage(controller, message);
		return resultsFuture;
	}

	@Override
	public void send(
			final ServiceProto.ServiceRef destination,
			final AbstractMessage message,
			final Class messageClass,
			final Optional<Expiration> expirationOptional) {
		final byte[] messageId = MessageUtils.randomMessageId(24);
		final MessageController controller = new DefaultMessageController(
				serviceRef,
				destination,
				ContentTypes.fromJsonClass(messageClass),
				Optional.of(messageId),
				Optional.<byte[]>absent(),
				expirationOptional.isPresent() ?
						Optional.of(expirationOptional.get().getExpiration()) : Optional.<DateTime>absent());
		serviceRegistry.sendMessage(controller, message);
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		final Optional<byte[]> messageId = controller.getCorrelationId();
		if (messageId.isPresent()) {
			final Pair<SettableFuture<Message>, DateTime> resultsFuturePair = resultsFutures.get(
					Arrays.hashCode(messageId.get()));
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
