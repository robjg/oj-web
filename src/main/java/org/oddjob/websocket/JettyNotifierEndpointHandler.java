package org.oddjob.websocket;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.remote.RemoteNotifier;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Provide a Handler for Jetty that provides the {@link NotifierServerEndpoint}.
 */
public class JettyNotifierEndpointHandler implements ValueFactory<Handler> {

    private RemoteNotifier remoteNotifier;

    @Override
    public Handler toValue() {

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath( "/" );

        ServerEndpointConfig config = ServerEndpointConfig.Builder
                .create(NotifierServerEndpoint.class, "/notifier")
                .configurator(new NotifierConfigurator(remoteNotifier))
                .build();

        WebSocketServerContainerInitializer
                .configure(context, (ctxt, container) -> container.addEndpoint(config));

        return context;
    }

    public RemoteNotifier getRemoteNotifier() {
        return remoteNotifier;
    }

    public void setRemoteNotifier(RemoteNotifier remoteNotifier) {
        this.remoteNotifier = remoteNotifier;
    }
}
