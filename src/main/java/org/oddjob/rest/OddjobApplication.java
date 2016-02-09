package org.oddjob.rest;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

/**
 * Main entry point into the Web Service. See 
 * <a href="https://jax-rs-spec.java.net/nonav/2.0-rev-a/apidocs/javax/ws/rs/core/Application.html">Application</>. 
 * 
 * 
 * @author rob
 *
 */
public class OddjobApplication extends Application {
	
	public static final String ROOT_ATTRIBUTE_NAME = "oj-root-node";
	
	private Set<Object> services;
	
	/** The standard Servlet Context. Injected by the Web Service Framework. */
	@Context
	private ServletContext servletContext;
	
	@Override
	public synchronized Set<Object> getSingletons() {
		
		if (services == null) {
			services = new HashSet<>();

			services.add(new OddjobApiImpl(
					(WebRoot) servletContext.getAttribute(ROOT_ATTRIBUTE_NAME)));
			
			return services;
		}
		else {
			return services;
		}
	}

}
