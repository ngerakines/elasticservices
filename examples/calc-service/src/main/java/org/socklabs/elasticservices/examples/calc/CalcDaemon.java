package org.socklabs.elasticservices.examples.calc;

import org.apache.commons.daemon.DaemonContext;
import org.socklabs.elasticservices.core.config.PropertiesConfig;
import org.socklabs.elasticservices.core.config.RabbitMqConfig;
import org.socklabs.elasticservices.core.config.ServiceConfig;
import org.socklabs.elasticservices.core.config.WorkConfig;
import org.socklabs.elasticservices.core.daemon.AbstractDaemon;
import org.socklabs.elasticservices.core.daemon.SimpleDaemonContext;
import org.socklabs.elasticservices.examples.calc.service.CalcServiceConfig;
import org.socklabs.elasticservices.gossip.GossipServiceConfig;

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
