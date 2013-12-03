package org.socklabs.elasticservices.core.daemon;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractDaemon implements Daemon {

    static {
        System.setProperty("archaius.dynamicPropertyFactory.registerConfigWithJMX", "true");
    }

    private ApplicationContext applicationContext;

    @Override
    public void init(final DaemonContext context)
            throws Exception {
        applicationContext = new AnnotationConfigApplicationContext(modules());
    }

    @Override
    public void start()
            throws Exception {
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public void destroy() {

    }

    protected abstract Class[] modules();

}
