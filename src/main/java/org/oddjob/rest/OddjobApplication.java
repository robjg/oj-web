package org.oddjob.rest;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

public class OddjobApplication extends Application {
	
	public static final String ROOT_NODE_NAME = "oj-root-node";
	
	private Set<Object> services;
	
	@Context
	private ServletContext servletContext;
	
	@Override
	public synchronized Set<Object> getSingletons() {
		
		if (services == null) {
			services = new HashSet<>();

			services.add(new OddjobApiImpl(
					servletContext.getAttribute(ROOT_NODE_NAME)));
			
			return services;
		}
		else {
			return services;
		}
	}

}
