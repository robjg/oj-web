package org.oddjob.rest;

import java.io.File;

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

	private final File uploadDirectory;
	
	public WebRoot(Object rootComponent, ArooaSession arooaSession, File fileUploadDir) {
		if (rootComponent == null) {
			throw new NullPointerException("No Root Component.");
		}
		if (arooaSession == null) {
			throw new NullPointerException("No ArooaSession.");
		}
		
		this.rootComponent = rootComponent;
		this.arooaSession = arooaSession;
		this.uploadDirectory = fileUploadDir;
	}
	
	public ArooaSession getArooaSession() {
		return arooaSession;
	}
	
	public Object getRootComponent() {
		return rootComponent;
	}
	
	public File getUploadDirectory() {
		return uploadDirectory;
	}
}
