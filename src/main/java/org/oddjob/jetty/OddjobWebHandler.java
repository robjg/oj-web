package org.oddjob.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
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
 * @oddjob.description Provide the Oddjob Web Service. The actual service API is defined in 
 * {@link OddjobApi}.
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
	 * @oddjob.required No.
	 */
	private volatile String servicePath;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The class path to the html files for oddjob web.
	 * @oddjob.required No.
	 */
	private volatile String webappResource;
	
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
				new WebRoot(root, session));

		ServletHolder servletHolder = new ServletHolder(
				new HttpServletDispatcher());
		servletHolder.setInitParameter("javax.ws.rs.Application", 
				"org.oddjob.rest.OddjobApplication");
		
		contextHandler.addServlet(servletHolder, servicePath);
		
		Handler handler;
		
		if (serviceOnly) {
			handler = contextHandler;
		}
		else {
			ResourceHandler resourceHandler = new ResourceHandler();
			resourceHandler.setBaseResource(Resource.newClassPathResource(webappResource));
			resourceHandler.setWelcomeFiles(new String[] { "index.html" });
			
			HandlerList handlers = new HandlerList();
			handlers.addHandler(resourceHandler);
			handlers.addHandler(contextHandler);
			
			handler = handlers;
		}
		
		return handler;
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

	public boolean isServiceOnly() {
		return serviceOnly;
	}
	
	public String getServicePath() {
		return servicePath;
	}

	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
