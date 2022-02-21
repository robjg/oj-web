package org.oddjob.web;

import org.eclipse.jetty.server.Handler;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.jetty.MultipartConfigParameters;
import org.oddjob.jmx.general.RemoteBridge;
import org.oddjob.jmx.server.JmxServer;

import javax.management.MBeanServerConnection;
import java.io.File;
import java.util.Objects;

/**
 * @oddjob.description Provide a Jetty Server Handler that connects to a local JMX implementation.
 * <p>
 *     This is a Work In Progress for the new React-JS front end.
 * </p>
 */
public class WebServerHandlerJmx implements ValueFactory<Handler>, ArooaSessionAware {

    private volatile ArooaSession session;

    /**
     * @oddjob.property
     * @oddjob.description An Oddjob JMX Server.
     * @oddjob.required Yes, for the time being. Will default soon.
     */
    private volatile JmxServer jmxServer;

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

    private volatile ClassLoader classLoader;

    @ArooaHidden
    @Override
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public Handler toValue() {

        JmxServer jmxServer = Objects.requireNonNull(this.jmxServer);

        MBeanServerConnection beanServer = jmxServer.getServerConnection();

        RemoteBridge remoteBridge = new RemoteBridge(
                beanServer);

        WebServerHandler webServerHandler = new WebServerHandler();
        webServerHandler.setClassLoader(classLoader);
        webServerHandler.setRemoteConnection(remoteBridge);
        webServerHandler.setIdMappings(jmxServer.getRemoteIdMappings());
        webServerHandler.setArooaSession(session);
        webServerHandler.setMultiPartConfig(multiPartConfig);
        webServerHandler.setUploadDirectory(uploadDirectory);
        webServerHandler.setAllowCrossOrigin(allowCrossOrigin);
        return webServerHandler.toValue();
    }

    public JmxServer getJmxServer() {
        return jmxServer;
    }

    public void setJmxServer(JmxServer jmxServer) {
        this.jmxServer = jmxServer;
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
