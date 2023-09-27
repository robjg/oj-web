package org.oddjob.web;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;
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
import org.oddjob.web.gson.GsonRemoteConnection;
import org.oddjob.web.gson.GsonUtil;
import org.oddjob.websocket.NotifierConfigurator;
import org.oddjob.websocket.NotifierServerEndpoint;

import javax.servlet.DispatcherType;
import javax.websocket.server.ServerEndpointConfig;
import java.io.File;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

/**
 * Provide Jetty handler for an HTTP and Web Socket Remote Connection. Designed as
 * an Oddjob Type, but currently not exposed in the Arooa descriptor.
 * <p>
 *     This handler also includes the REST API. It probably shouldn't and so will be
 *     removed at some point. It can be included separately anyway using the
 *     {@link OddjobRestHandler}.
 * </p>
 *
 * @see WebServerHandlerJmx
 */
public class WebServerHandler implements ValueFactory<Handler>, ArooaSessionAware {

    private RemoteConnection remoteConnection;

    private RemoteIdMappings idMappings;

    private ArooaSession session;

    /**
     * Set parameters for MultiPartConfig so that file upload from a form works.
     * If not set then defaults are used.
     */
    private volatile MultipartConfigParameters multiPartConfig;

    /**
     * Upload directory. Required for an action form that specifies a file.
     * If not set then defaults to the tmp dir.
     */
    private volatile File uploadDirectory;

    /**
     * Is cross-origin content allowed?
     * Defaults to false.
     */
    private volatile boolean allowCrossOrigin;

    /**
     * The classloader passed to Jetty. If not set then Jetty and RESTEasy use the Thread context
     * classloader. This is set by Oddjob's service adapter to be the classloader that loaded
     * the component that is using this handler which will be the Oddball classloader. Setting
     * this classloader will be complicated as it may require the Oddball classloader as a
     * parent.
     */
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

        Gson gson = GsonUtil.createGson(session);

        // Invoker

        contextHandler.setAttribute(InvokerServlet.REMOTE_INVOKER,
                GsonRemoteConnection.to(remoteConnection, gson));

        ServletHolder servletHolder = new ServletHolder(
                new InvokerServlet());

        contextHandler.addServlet(servletHolder, "/invoke");

        // Notifier

        ServerEndpointConfig config = ServerEndpointConfig.Builder
                .create(NotifierServerEndpoint.class, "/notifier")
                .configurator(new NotifierConfigurator(remoteConnection, gson))
                .build();

        JavaxWebSocketServletContainerInitializer
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
