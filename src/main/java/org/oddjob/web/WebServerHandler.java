package org.oddjob.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.http.InvokerServlet;
import org.oddjob.remote.RemoteConnection;
import org.oddjob.websocket.NotifierConfigurator;
import org.oddjob.websocket.NotifierServerEndpoint;

import javax.websocket.server.ServerEndpointConfig;
import java.util.Objects;

/**
 * Provide Jetty handler for an http and web socket Remote Connection.
 */
public class WebServerHandler implements ValueFactory<Handler> {

    private RemoteConnection remoteConnection;

    @Override
    public Handler toValue() {

        RemoteConnection remoteConnection = Objects.requireNonNull(this.remoteConnection);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath( "/" );

        context.setAttribute(InvokerServlet.REMOTE_INVOKER,
                remoteConnection);

        ServletHolder servletHolder = new ServletHolder(
                new InvokerServlet());

        context.addServlet(servletHolder, "/invoke");

        ServerEndpointConfig config = ServerEndpointConfig.Builder
                .create(NotifierServerEndpoint.class, "/notifier")
                .configurator(new NotifierConfigurator(remoteConnection))
                .build();

        WebSocketServerContainerInitializer
                .configure(context, (ctxt, container) -> container.addEndpoint(config));

        return context;
    }

    public RemoteConnection getRemoteConnection() {
        return remoteConnection;
    }

    public void setRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }
}
