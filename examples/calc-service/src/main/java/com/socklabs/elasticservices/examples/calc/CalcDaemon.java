package com.socklabs.elasticservices.examples.calc;

import org.apache.commons.daemon.DaemonContext;

import com.socklabs.elasticservices.core.config.PropertiesConfig;
import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.config.WorkConfig;
import com.socklabs.elasticservices.core.daemon.AbstractDaemon;
import com.socklabs.elasticservices.core.daemon.SimpleDaemonContext;
import com.socklabs.elasticservices.examples.calc.service.CalcServiceConfig;
import com.socklabs.elasticservices.gossip.GossipServiceConfig;
import com.socklabs.elasticservices.http.client.HttpClientConfig;
import com.socklabs.elasticservices.rabbitmq.RabbitMqConfig;

public class CalcDaemon extends AbstractDaemon {

	static {
		System.setProperty("archaius.dynamicPropertyFactory.registerConfigWithJMX", "true");
	}

	public static void main(final String[] argv) throws Exception {
		final DaemonContext daemonContext = new SimpleDaemonContext(argv);
		final CalcDaemon pspDirector = new CalcDaemon();
		pspDirector.init(daemonContext);
		pspDirector.start();
	}

	@Override
	protected Class[] modules() {
		return new Class[]{
				PropertiesConfig.class,
				CalcPropertiesConfig.class,
				RabbitMqConfig.class,
				HttpClientConfig.class,
				ServiceConfig.class,
				WorkConfig.class,
				GossipServiceConfig.class,
				CalcServiceConfig.class};
	}

}
