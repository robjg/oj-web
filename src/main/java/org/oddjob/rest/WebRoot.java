package org.oddjob.rest;

import org.oddjob.arooa.ArooaSession;

/**
 * Wrapper round an Oddjob component and a Session. Used to pass this information between
 * the Servlet and the Web Service implementation.
 * 
 * @author rob
 *
 */
public class WebRoot {

	private final Object rootComponent;
	
	private final ArooaSession arooaSession;
	
	public WebRoot(Object rootComponent, ArooaSession arooaSession) {
		if (rootComponent == null) {
			throw new NullPointerException("No Root Component.");
		}
		if (arooaSession == null) {
			throw new NullPointerException("No ArooaSession.");
		}
		
		this.rootComponent = rootComponent;
		this.arooaSession = arooaSession;
	}
	
	public ArooaSession getArooaSession() {
		return arooaSession;
	}
	
	public Object getRootComponent() {
		return rootComponent;
	}
}
