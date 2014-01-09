package com.socklabs.elasticservices.activemq;

import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.AbstractMessage;
import com.socklabs.elasticservices.core.misc.Ref;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.*;

/**
 * Created by nick.gerakines on 1/9/14.
 */
public class ActiveMqTransportClient implements TransportClient, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqTransportClient.class);
	private static final BaseEncoding B16 = BaseEncoding.base16();

	private final ActiveMqTransportRef transportRef;
	private final Connection connection;
	private final Session session;
	private final Destination destination;
	private final MessageProducer messageProducer;

	public ActiveMqTransportClient(final Ref ref, final ConnectionFactory connectionFactory) throws JMSException {
		this.transportRef = new ActiveMqTransportRef(ref);

		this.connection = connectionFactory.createConnection();
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		this.destination = session.createQueue(transportRef.getQueue());
		this.messageProducer = session.createProducer(destination);

	}

	@Override
	public void send(MessageController messageController, AbstractMessage message) {
		final Optional<BytesMessage> bytesMessageOptional = createMessage();
		if (!bytesMessageOptional.isPresent()) {
			LOGGER.error("Could not create bytes message.");
			return;
		}
		final BytesMessage bytesMessage = bytesMessageOptional.get();
		try {
			messageProducer.send(bytesMessage);
		} catch (final JMSException e) {
			LOGGER.error("JMSException raised when sending message.", e);
		}
	}

	@Override
	public Ref getRef() {
		return transportRef.getRef();
	}

	private Optional<BytesMessage> createMessage() {
		return Optional.absent();
	}

	@Override
	public void destroy() throws Exception {
		messageProducer.close();
		session.close();
		connection.close();
	}

	public void init() throws Exception {
		connection.start();
	}

}
