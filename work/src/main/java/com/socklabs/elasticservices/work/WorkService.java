package com.socklabs.elasticservices.work;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.socklabs.elasticservices.core.ServiceProto;
import com.socklabs.elasticservices.core.message.ContentTypes;
import com.socklabs.elasticservices.core.message.MessageFactory;
import com.socklabs.elasticservices.core.service.AbstractService;
import com.socklabs.elasticservices.core.service.MessageController;
import com.socklabs.elasticservices.core.service.ServiceRegistry;
import com.socklabs.elasticservices.core.work.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 * Created by ngerakines on 12/16/13.
 */
public class WorkService extends AbstractService implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkService.class);

	private final ServiceRegistry serviceRegistry;

	private ApplicationContext applicationContext;

	protected WorkService(final List<MessageFactory> messageFactories,
						  final ServiceProto.ServiceRef serviceRef,
						  final ServiceRegistry serviceRegistry) {
		super(serviceRef, messageFactories);
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		if (messageHasExpired(controller)) {
			return;
		}
		if (message instanceof WorkServiceProto.ListRequest) {
			// final WorkServiceProto.ListRequest request = (WorkServiceProto.ListRequest) message;

			final WorkServiceProto.ListResponse.Builder responseBuilder = WorkServiceProto.ListResponse.newBuilder();

			if (applicationContext != null) {
				final Map<String, Work> beans = applicationContext.getBeansOfType(Work.class);
				for (final Map.Entry<String, Work> entry : beans.entrySet()) {
					final Work work = entry.getValue();
					responseBuilder.addWorkInfo(
							WorkServiceProto.WorkInfo.newBuilder()
									.setId(work.getId())
									.setPhase(work.getPhase().toString()));
				}
			} else {
				LOGGER.warn("Application context was not set.");
			}

			serviceRegistry.reply(
					controller,
					getServiceRef(),
					responseBuilder.build(),
					ContentTypes.fromClass(WorkServiceProto.ListResponse.class));
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
