package org.oddjob.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

/**
 * Provide a simple Http Connector.
 *
 * Todo: Add all the properties that Jetty supports.
 */
public class HttpServerModifier implements JettyServerModifier {

    private volatile int port;

    @Override
    public void modify(Server server) {

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "HttpConfiguration{" +
                "port=" + port +
                '}';
    }
}
