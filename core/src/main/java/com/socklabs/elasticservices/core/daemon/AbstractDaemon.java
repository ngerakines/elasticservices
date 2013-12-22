package com.socklabs.elasticservices.core.daemon;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractDaemon implements Daemon {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDaemon.class);

	@Override
	public void init(final DaemonContext context) throws Exception {
		final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(modules());
		final Properties properties = buildOverrideProperties(context.getArguments());
		applicationContext
				.getEnvironment()
				.getPropertySources()
				.addLast(new PropertiesPropertySource("override", properties));
	}

	protected abstract Class[] modules();

	@Override
	public void start() throws Exception {}

	@Override
	public void stop() throws Exception {}

	@Override
	public void destroy() {}

	private Properties buildOverrideProperties(final String[] args) throws ConfigurationException {
		final Properties properties = new Properties();
		if (args.length > 0) {
			final String fileName = args[0];
			try {
				final InputStream inputStream = new FileInputStream(fileName);
				properties.load(inputStream);
			} catch (final IOException e) {
				LOGGER.error("Could not load properties from: " + fileName, e);
			}
		}
		return properties;
	}

}
