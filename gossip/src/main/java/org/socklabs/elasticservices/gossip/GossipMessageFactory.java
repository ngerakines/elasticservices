package org.socklabs.elasticservices.gossip;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import org.socklabs.elasticservices.core.message.AbstractMessageFactory;

public class GossipMessageFactory extends AbstractMessageFactory {

    public GossipMessageFactory() {
        super(Lists.newArrayList(GossipServiceProto.ComponentOnline.class.getName(),
                GossipServiceProto.ComponentStatus.class.getName()));
    }

    @Override
    protected Optional<Message> getPrototype(final String messageClass) {
        if (messageClass.endsWith(GossipServiceProto.ComponentOnline.class.getName())) {
            return Optional.<Message>of(GossipServiceProto.ComponentOnline.getDefaultInstance());
        } else if (messageClass.endsWith(GossipServiceProto.ComponentStatus.class.getName())) {
            return Optional.<Message>of(GossipServiceProto.ComponentStatus.getDefaultInstance());
        }
        return Optional.absent();
    }

}
