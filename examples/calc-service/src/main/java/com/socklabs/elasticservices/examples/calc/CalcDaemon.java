package com.socklabs.elasticservices.examples.calc;

import org.apache.commons.daemon.DaemonContext;
import com.socklabs.elasticservices.core.config.PropertiesConfig;
import com.socklabs.elasticservices.core.config.RabbitMqConfig;
import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.config.WorkConfig;
import com.socklabs.elasticservices.core.daemon.AbstractDaemon;
import com.socklabs.elasticservices.core.daemon.SimpleDaemonContext;
import com.socklabs.elasticservices.examples.calc.service.CalcServiceConfig;
import com.socklabs.elasticservices.gossip.GossipServiceConfig;

public class CalcDaemon extends AbstractDaemon {

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
				ServiceConfig.class,
				WorkConfig.class,
				GossipServiceConfig.class,
				CalcServiceConfig.class};
	}

}
