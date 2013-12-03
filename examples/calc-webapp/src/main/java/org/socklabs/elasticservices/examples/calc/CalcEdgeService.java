package org.socklabs.elasticservices.examples.calc;

import com.google.protobuf.Message;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.edge.AbstractEdgeService;
import org.socklabs.elasticservices.core.edge.EdgeManager;
import org.socklabs.elasticservices.core.message.MessageFactory;
import org.socklabs.elasticservices.core.service.MessageController;

import java.util.List;

public class CalcEdgeService extends AbstractEdgeService {

    public CalcEdgeService(
            final ServiceProto.ServiceRef serviceRef,
            final EdgeManager edgeManager,
            final List<MessageFactory> messageFactories) {
        super(serviceRef, edgeManager, messageFactories);
    }

    @Override
    protected boolean canHandleMessage(MessageController controller, Message message) {
        return true;
    }

}
