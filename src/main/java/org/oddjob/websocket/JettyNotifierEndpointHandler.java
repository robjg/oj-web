package org.oddjob.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.remote.RemoteNotifier;
import org.oddjob.web.gson.GsonUtil;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Provide a Handler for Jetty that provides the {@link NotifierServerEndpoint}.
 * <p>
 *     This code is duplicated in {@link org.oddjob.web.WebServerHandler}. Not sure when this might be used
 *     on it's own. Should it be removed?
 * </p>
 *
 */
public class JettyNotifierEndpointHandler implements ValueFactory<Handler>, ArooaSessionAware {

    private ArooaSession arooaSession;

    private RemoteNotifier remoteNotifier;

    private ClassLoader classLoader;

    @Override
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    @Override
    public Handler toValue() {

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath( "/" );
        context.setClassLoader(classLoader);

        Gson gson = GsonUtil.createGson(arooaSession);

        ServerEndpointConfig config = ServerEndpointConfig.Builder
                .create(NotifierServerEndpoint.class, "/notifier")
                .configurator(new NotifierConfigurator(remoteNotifier, gson))
                .build();

        JavaxWebSocketServletContainerInitializer
                .configure(context, (ctxt, container) -> container.addEndpoint(config));

        return context;
    }

    public RemoteNotifier getRemoteNotifier() {
        return remoteNotifier;
    }

    public void setRemoteNotifier(RemoteNotifier remoteNotifier) {
        this.remoteNotifier = remoteNotifier;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Inject
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
