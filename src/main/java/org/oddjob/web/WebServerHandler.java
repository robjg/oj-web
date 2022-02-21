package org.oddjob.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.http.InvokerServlet;
import org.oddjob.jetty.MultipartConfigParameters;
import org.oddjob.jetty.OddjobRestHandler;
import org.oddjob.jmx.RemoteIdMappings;
import org.oddjob.remote.RemoteConnection;
import org.oddjob.rest.OddjobApplication;
import org.oddjob.websocket.NotifierConfigurator;
import org.oddjob.websocket.NotifierServerEndpoint;

import javax.servlet.DispatcherType;
import javax.websocket.server.ServerEndpointConfig;
import java.io.File;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

/**
 * Provide Jetty handler for an http and web socket Remote Connection.
 */
public class WebServerHandler implements ValueFactory<Handler>, ArooaSessionAware {

    private RemoteConnection remoteConnection;

    private RemoteIdMappings idMappings;

    private ArooaSession session;

    /**
     * @oddjob.property
     * @oddjob.description Set parameters for MultiPartConfig so that file upload from a form works.
     * @oddjob.required No. Defaults are used.
     */
    private volatile MultipartConfigParameters multiPartConfig;

    /**
     * @oddjob.property
     * @oddjob.description Upload directory. Required for an action form that specifies a file.
     * @oddjob.required No. Defaults tmp dir.
     */
    private volatile File uploadDirectory;

    /**
     * @oddjob.property
     * @oddjob.description Is cross origin content allowed?
     * @oddjob.required No. Default to false.
     */
    private volatile boolean allowCrossOrigin;

    private ClassLoader classLoader;

    @ArooaHidden
    @Override
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public Handler toValue() {

        RemoteConnection remoteConnection = Objects.requireNonNull(this.remoteConnection);
        RemoteIdMappings idMappings = Objects.requireNonNull(this.idMappings);

        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath( "/" );
        contextHandler.setClassLoader(classLoader);

        // Invoker

        contextHandler.setAttribute(InvokerServlet.REMOTE_INVOKER,
                remoteConnection);

        ServletHolder servletHolder = new ServletHolder(
                new InvokerServlet());

        contextHandler.addServlet(servletHolder, "/invoke");

        // Notifier

        ServerEndpointConfig config = ServerEndpointConfig.Builder
                .create(NotifierServerEndpoint.class, "/notifier")
                .configurator(new NotifierConfigurator(remoteConnection))
                .build();

        WebSocketServerContainerInitializer
                .configure(contextHandler, (ctxt, container) -> container.addEndpoint(config));

        // Api

        contextHandler.setAttribute(
                OddjobApplication.ID_MAPPINGS_ATTRIBUTE_NAME,
                idMappings);
        contextHandler.setAttribute(
                OddjobApplication.SESSION_ATTRIBUTE_NAME, session);
        contextHandler.setAttribute(
                OddjobApplication.UPLOAD_DIR_ATTRIBUTE_NAME,
                Optional.ofNullable(uploadDirectory)
                        .orElseGet(() -> new File(System.getProperty("java.io.tmpdir"))));

        contextHandler.addServlet(OddjobRestHandler.wsServletHolder(
                Optional.ofNullable(multiPartConfig)
                        .orElse(new MultipartConfigParameters())
                        .toMultipartConfigElement()
                ),
                OddjobRestHandler.SERVICE_PATH);

        if (allowCrossOrigin) {
            contextHandler.addFilter(OddjobRestHandler.crossOriginFilter(),
                    "/*",
                    EnumSet.of(DispatcherType.REQUEST));
        }

        return contextHandler;
    }

    public RemoteConnection getRemoteConnection() {
        return remoteConnection;
    }

    public void setRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    public RemoteIdMappings getIdMappings() {
        return idMappings;
    }

    public void setIdMappings(RemoteIdMappings idMappings) {
        this.idMappings = idMappings;
    }

    public MultipartConfigParameters getMultiPartConfig() {
        return multiPartConfig;
    }

    public void setMultiPartConfig(MultipartConfigParameters multiPartConfig) {
        this.multiPartConfig = multiPartConfig;
    }

    public File getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(File uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public boolean isAllowCrossOrigin() {
        return allowCrossOrigin;
    }

    public void setAllowCrossOrigin(boolean allowCrossOrigin) {
        this.allowCrossOrigin = allowCrossOrigin;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
