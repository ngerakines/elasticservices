package org.socklabs.elasticservices.examples.calc.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.message.ContentTypes;
import org.socklabs.elasticservices.core.message.MessageFactory;
import org.socklabs.elasticservices.core.message.MessageUtils;
import org.socklabs.elasticservices.core.service.DefaultMessageController;
import org.socklabs.elasticservices.core.service.MessageController;
import org.socklabs.elasticservices.core.service.Service;
import org.socklabs.elasticservices.core.service.ServiceRegistry;
import org.socklabs.elasticservices.examples.calc.CalcServiceProto;

import java.util.List;

public class CalcService implements Service {

    private final ServiceProto.ServiceRef serviceRef;

    private final ServiceRegistry serviceRegistry;

    private final MessageFactory calcMessageFactory;

    public CalcService(
            final ServiceProto.ServiceRef serviceRef,
            final ServiceRegistry serviceRegistry
    ) {
        this.serviceRef = serviceRef;
        this.serviceRegistry = serviceRegistry;

        this.calcMessageFactory = new CalcMessageFactory();
    }

    @Override
    public ServiceProto.ServiceRef getServiceRef() {
        return serviceRef;
    }

    @Override
    public List<MessageFactory> getMessageFactories() {
        return ImmutableList.of(calcMessageFactory);
    }

    @Override
    public void handleMessage(final MessageController controller, final Message message) {
        if (message instanceof CalcServiceProto.Add) {
            int sum = 0;
            for (final Integer value : ((CalcServiceProto.Add) message).getValuesList()) {
                sum += value;
            }
            final CalcServiceProto.Result result = CalcServiceProto.Result.newBuilder().setValue(sum).build();

            final MessageController outboundController = new DefaultMessageController(
                    serviceRef,
                    controller.getSender(),
                    ContentTypes.fromJsonClass(CalcServiceProto.Result.class),
                    Optional.of(MessageUtils.randomMessageId(24)),
                    controller.getMessageId());
            serviceRegistry.sendMessage(outboundController, result);
        }
    }

}
