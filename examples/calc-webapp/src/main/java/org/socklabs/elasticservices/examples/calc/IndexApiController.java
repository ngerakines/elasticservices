package org.socklabs.elasticservices.examples.calc;

import com.google.common.primitives.Ints;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socklabs.elasticservices.core.ServiceProto;
import org.socklabs.elasticservices.core.collection.PartitionUtil;
import org.socklabs.elasticservices.core.edge.EdgeManager;
import org.socklabs.elasticservices.core.service.ServiceRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Controller @RequestMapping("/api")
public class IndexApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexApiController.class);

	@Resource
	private ServiceRegistry serviceRegistry;

	@Resource(name = "calcEdgeManager")
	private EdgeManager edgeManager;

	@ResponseBody @RequestMapping(value = "/calc", method = RequestMethod.GET)
	public String handleQuery(final HttpServletRequest request) {
		final CalcServiceProto.Add.Builder addBuilder = CalcServiceProto.Add.newBuilder();
		for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			for (final String value : entry.getValue()) {
				final Integer intValue = Ints.tryParse(value);
				if (intValue != null) {
					addBuilder.addValues(intValue);
				}
			}
		}
		final ServiceProto.ServiceRef serviceRef = selectDestination();
		final Future<Message> resultfuture = edgeManager.execute(
				serviceRef, addBuilder.build(), CalcServiceProto.Add.class);
		try {
			final Message message = resultfuture.get(10, TimeUnit.SECONDS);
			if (message instanceof CalcServiceProto.Result) {
				return JsonFormat.printToString(message);
			}
		} catch (final TimeoutException | ExecutionException | InterruptedException e) {
			LOGGER.error("Error getting result.", e);
		}
		return "ERROR";
	}

	private ServiceProto.ServiceRef selectDestination() {
		final List<ServiceProto.ServiceRef> services = serviceRegistry.getServices("calc");
		return PartitionUtil.random(services);
	}

}
