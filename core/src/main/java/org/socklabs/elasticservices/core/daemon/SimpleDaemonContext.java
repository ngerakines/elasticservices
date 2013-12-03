package org.socklabs.elasticservices.core.daemon;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;

public class SimpleDaemonContext implements DaemonContext {

    private final String[] args;
    private final DaemonController daemonController;

    public SimpleDaemonContext(final String[] args) {
        this.args = args;
        this.daemonController = new SimpleDaemonController();
    }

    @Override
    public DaemonController getController() {
        return daemonController;
    }

    @Override
    public String[] getArguments() {
        return args;
    }
}
