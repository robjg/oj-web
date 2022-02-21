package org.oddjob.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.rest.OddjobApi;
import org.oddjob.rest.OddjobApplication;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

/**
 * @author rob
 * @oddjob.description Provide the Oddjob REST Service.
 * <p>
 * The actual service API is defined in {@link OddjobApi}.
 * @oddjob.example Provide an Oddjob web client.
 * <p>
 * {@oddjob.xml.resource org/oddjob/jetty/OddjobWeb.xml}
 * @oddjob.example Provide an Oddjob web service only without the client.
 * <p>
 * {@oddjob.xml.resource org/oddjob/jetty/OddjobWebService.xml}
 */
public class OddjobRestHandler
        implements ValueFactory<Handler>, ArooaSessionAware {

    public static final String CONTEXT_PATH = "/";

    public static final String SERVICE_PATH = "/api/*";

    /**
     * @oddjob.property
     * @oddjob.description The root Oddjob component to expose via the web service.
     * @oddjob.required Yes.
     */
    private volatile Object root;

    /**
     * @oddjob.property
     * @oddjob.description The context path.
     * @oddjob.required No.
     */
    private volatile String contextPath;

    /**
     * @oddjob.property
     * @oddjob.description The context path for the Oddjob web service.
     * @oddjob.required No. Defaults to '/'
     */
    private volatile String servicePath;

    /**
     * @oddjob.property
     * @oddjob.description Is cross origin content allowed?
     * @oddjob.required No. Default to false.
     */
    private volatile boolean allowCrossOrigin;

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
     * Set by the container.
     */
    private volatile ArooaSession session;

    @Override
    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public Handler toValue() {

        Object root = Objects.requireNonNull(this.root, "No root job.");

        String contextPath = this.contextPath;
        if (contextPath == null) {
            contextPath = CONTEXT_PATH;
        }

        String servicePath = this.servicePath;
        if (servicePath == null) {
            servicePath = SERVICE_PATH;
        }

        ServletContextHandler contextHandler = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        contextHandler.setContextPath(contextPath);
        ServletContext servletContext = contextHandler.getServletContext();
        servletContext.setAttribute(
                OddjobApplication.ROOT_ATTRIBUTE_NAME,
                root);
        servletContext.setAttribute(
                OddjobApplication.SESSION_ATTRIBUTE_NAME, session);
        servletContext.setAttribute(
                OddjobApplication.UPLOAD_DIR_ATTRIBUTE_NAME,
                Optional.ofNullable(uploadDirectory)
                        .orElseGet(() -> new File(System.getProperty("java.io.tmpdir"))));


        contextHandler.addServlet(wsServletHolder(
                Optional.ofNullable(multiPartConfig)
                        .orElse(new MultipartConfigParameters())
                        .toMultipartConfigElement()
                ),
                servicePath);

        if (allowCrossOrigin) {
            contextHandler.addFilter(crossOriginFilter(), "/*", EnumSet.of(DispatcherType.REQUEST));
        }

        return contextHandler;
    }

    public static ServletHolder wsServletHolder(MultipartConfigElement multipartConfigElement) {

        ServletHolder servletHolder = new ServletHolder(
                new HttpServletDispatcher());
        servletHolder.setInitParameter("javax.ws.rs.Application",
                "org.oddjob.rest.OddjobApplication");

        servletHolder.getRegistration().setMultipartConfig(multipartConfigElement);

        return servletHolder;
    }

    public static FilterHolder crossOriginFilter() {
        FilterHolder holder = new FilterHolder(CrossOriginFilter.class);
        holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        holder.setName("cross-origin");

        return holder;
    }

    @ArooaAttribute
    public void setRoot(Object root) {
        this.root = root;
    }

    public Object getRoot() {
        return root;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public boolean isAllowCrossOrigin() {
        return allowCrossOrigin;
    }

    public void setAllowCrossOrigin(boolean allowCrossOrigin) {
        this.allowCrossOrigin = allowCrossOrigin;
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

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

