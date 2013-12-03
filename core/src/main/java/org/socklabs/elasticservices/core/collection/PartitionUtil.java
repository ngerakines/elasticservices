package org.socklabs.elasticservices.core.collection;

import com.google.common.base.Preconditions;
import org.socklabs.elasticservices.core.ServiceProto;

import java.util.List;

public class PartitionUtil {

    public static ServiceProto.ServiceRef random(final List<ServiceProto.ServiceRef> serviceRefs) {
        Preconditions.checkArgument(serviceRefs.size() > 0);
        return RandomUtils.randomFromListOf(serviceRefs);
    }

    public static ServiceProto.ServiceRef next(final List<ServiceProto.ServiceRef> serviceRefs, final ServiceProto.ServiceRef last) {
        Preconditions.checkArgument(serviceRefs.size() > 0);
        if (serviceRefs.size() > 1) {
            boolean getNext = false;
            for (final ServiceProto.ServiceRef serviceRef : serviceRefs) {
                if (getNext) {
                    return serviceRef;
                }
                if (serviceRef.equals(last)) {
                    getNext = true;
                }
            }
        }
        return serviceRefs.get(0);
    }

    public static ServiceProto.ServiceRef partitioned(final List<ServiceProto.ServiceRef> serviceRefs, final Object key) {
        Preconditions.checkArgument(serviceRefs.size() > 0);
        Preconditions.checkArgument(key != null);
        if (serviceRefs.size() > 1) {
            int slot = Math.abs(key.hashCode()) % serviceRefs.size();
            return serviceRefs.get(slot);
        }
        return serviceRefs.get(0);
    }

}
