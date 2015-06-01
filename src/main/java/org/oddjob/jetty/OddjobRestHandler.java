package org.oddjob.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.rest.OddjobApplication;
import org.oddjob.rest.WebRoot;

public class OddjobRestHandler 
implements ValueFactory<Handler>, ArooaSessionAware {

	private volatile Object root;
	
	private volatile ArooaSession session;
	
	@Override
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Override
	public Handler toValue() throws ArooaConversionException {
		
		ServletContextHandler contextHandler = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/");
		contextHandler.getServletContext().setAttribute(
				OddjobApplication.ROOT_ATTRIBUTE_NAME, 
				new WebRoot(root, session));
		
		ServletHolder servletHolder = new ServletHolder(
				new HttpServletDispatcher());
		servletHolder.setInitParameter("javax.ws.rs.Application", 
				"org.oddjob.rest.OddjobApplication");
		
		contextHandler.addServlet(servletHolder, "/api/*");
		
		return contextHandler;
	}
	
	public void setRoot(Object root) {
		this.root = root;
	}
	
	public Object getRoot() {
		return root;
	}
}
