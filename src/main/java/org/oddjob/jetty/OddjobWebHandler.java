package org.oddjob.jetty;

import java.io.File;
import java.net.MalformedURLException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.rest.OddjobApi;
import org.oddjob.rest.OddjobApplication;
import org.oddjob.rest.WebRoot;

/**
 * @oddjob.description Provide the Oddjob Web Service and Oddjob Web interface.
 * <p>
 * The actual service API is defined in {@link OddjobApi}.
 * 
 * @oddjob.example
 * 
 * Provide an Oddjob web client.
 * 
 * {@oddjob.xml.resource org/oddjob/jetty/OddjobWeb.xml}
 * 
 * @oddjob.example
 * 
 * Provide an Oddjob web service only without the client.
 * 
 * {@oddjob.xml.resource org/oddjob/jetty/OddjobWebService.xml}
 * 
 * 
 * 
 * @author rob
 *
 */
public class OddjobWebHandler 
implements ValueFactory<Handler>, ArooaSessionAware {
	
	public static final String CONTEXT_PATH = "/";
	
	public static final String WEBAPP_RESOURCE = "org/oddjob/webapp";
	
	public static final String SERVICE_PATH = "/api/*";
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The root Oddjob component to expose via the web service.
	 * @oddjob.required Yes.
	 */
	private volatile Object root;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description Only enable the Oddjob Web Service, not the accompanying
	 * html.
	 * @oddjob.required No.
	 */
	private volatile boolean serviceOnly;
	
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
	 * @oddjob.description The class path to the html files for oddjob web.
	 * @oddjob.required No. Defaults to the webapp in the jar at org/oddjob/webapp.
	 */
	private volatile String webappResource;

	/** 
	 * @oddjob.property
	 * @oddjob.description The directory for the html files for oddjob web. Mainly used for 
	 * development to save stopping and starting Jetty.
	 * @oddjob.required No.
	 */
	private volatile File webappDir;
	
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
	

	private volatile File uploadDirectory = new File(System.getProperty("java.io.tmpdir"));
	
	/** Set by the container. */
	private volatile ArooaSession session;
	
	@Override
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Override
	public Handler toValue() throws ArooaConversionException {

		String contextPath = this.contextPath;
		if (contextPath == null) {
			contextPath = CONTEXT_PATH;
		}
		
		String servicePath = this.servicePath;
		if (servicePath == null) {
			servicePath = SERVICE_PATH;
		}
		
		String webappResource = this.webappResource;
		if (webappResource == null) {
			webappResource = WEBAPP_RESOURCE;
		}
		
		ServletContextHandler contextHandler = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		contextHandler.setContextPath(contextPath);
		contextHandler.getServletContext().setAttribute(
				OddjobApplication.ROOT_ATTRIBUTE_NAME, 
				new WebRoot(root, session, uploadDirectory));
		
		contextHandler.addServlet(wsServletHolder(), servicePath);
		
		if (allowCrossOrigin) {
			contextHandler.addFilter(crossOriginFilter(), "/*", EnumSet.of(DispatcherType.REQUEST));
		}
		
		Handler handler;
		
		if (serviceOnly) {
			handler = contextHandler;
		}
		else {
			HandlerList handlers = new HandlerList();
			handlers.addHandler(resourceHandler(webappResource));
			handlers.addHandler(contextHandler);
			
			handler = handlers;
		}
		
		return handler;
	}
	
	protected ServletHolder wsServletHolder() {
		
		ServletHolder servletHolder = new ServletHolder(
				new HttpServletDispatcher());
		servletHolder.setInitParameter("javax.ws.rs.Application", 
				"org.oddjob.rest.OddjobApplication");
		
		servletHolder.getRegistration().setMultipartConfig(multipartConfigElement());
		
		return servletHolder;
	}
	
	protected MultipartConfigElement multipartConfigElement() {
		
		String location = null;
		long maxFileSize = -1L;
		long maxRequestSize = -1L;
		int fileSizeThreshold = 0;
		
		if (multiPartConfig != null) {
			location = multiPartConfig.getLocation();
			maxFileSize = multiPartConfig.getMaxFileSize();
			maxRequestSize = multiPartConfig.getMaxRequestSize();
			fileSizeThreshold = multiPartConfig.getFileSizeThreshold();
		}
		
		return new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
	}
		
		
	protected FilterHolder crossOriginFilter() {
		FilterHolder holder = new FilterHolder(CrossOriginFilter.class);
		holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
		holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
		holder.setName("cross-origin");
		
		return holder;
	}
	
	protected Handler resourceHandler(String webappResource) {
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setBaseResource(webappDir == null ? 
				Resource.newClassPathResource(webappResource) :
				Resource.newResource(webappDir));
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		
		return resourceHandler;
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
	
	public void setServiceOnly(boolean serviceOnly) {
		this.serviceOnly = serviceOnly;
	}
	
	public String getWebappResource() {
		return webappResource;
	}

	public void setWebappResource(String webappResource) {
		this.webappResource = webappResource;
	}

	public File getWebappDir() {
		return webappDir;
	}

	public void setWebappDir(File webappDir) {
		this.webappDir = webappDir;
	}

	public boolean isServiceOnly() {
		return serviceOnly;
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

