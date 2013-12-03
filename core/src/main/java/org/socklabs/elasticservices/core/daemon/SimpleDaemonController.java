package org.socklabs.elasticservices.core.daemon;

import org.apache.commons.daemon.DaemonController;

public class SimpleDaemonController implements DaemonController {

    @Override
    public void shutdown() throws IllegalStateException {
    }

    @Override
    public void reload() throws IllegalStateException {
    }

    @Override
    public void fail() throws IllegalStateException {
    }

    @Override
    public void fail(String message) throws IllegalStateException {
    }

    @Override
    public void fail(Exception exception) throws IllegalStateException {
    }

    @Override
    public void fail(String message, Exception exception) throws IllegalStateException {
    }

}
