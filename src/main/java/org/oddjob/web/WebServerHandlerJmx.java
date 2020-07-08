package org.oddjob.web;

import org.eclipse.jetty.server.Handler;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.jmx.general.RemoteBridge;

import javax.management.MBeanServerConnection;
import java.lang.management.ManagementFactory;
import java.util.Optional;

/**
 * Provide a Jetty Server Handler that connects to a local JMX implementation.
 */
public class WebServerHandlerJmx implements ValueFactory<Handler> {

    private MBeanServerConnection beanServer;

    @Override
    public Handler toValue() {

        MBeanServerConnection beanServer = Optional.ofNullable(this.beanServer)
                .orElseGet(ManagementFactory::getPlatformMBeanServer);

        RemoteBridge remoteBridge = new RemoteBridge(
                beanServer);

        WebServerHandler webServerHandler = new WebServerHandler();
        webServerHandler.setRemoteConnection(remoteBridge);

        return webServerHandler.toValue();
    }

    public MBeanServerConnection getBeanServer() {
        return beanServer;
    }

    public void setBeanServer(MBeanServerConnection beanServer) {
        this.beanServer = beanServer;
    }
}
