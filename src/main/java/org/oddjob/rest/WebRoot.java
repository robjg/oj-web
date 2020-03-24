package org.oddjob.rest;

import java.io.File;
import java.util.Objects;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.describe.Describer;

/**
 * Wrapper round an Oddjob component and a Session. Used to pass this information between
 * the Servlet and the Web Service implementation.
 * 
 * @author rob
 *
 */
public class WebRoot {

	private final Object rootComponent;
	
	private final BeanDirectory beanDirectory;

	private final Describer describer;

	private final File uploadDirectory;
	
	public WebRoot(Object rootComponent, BeanDirectory beanDirectory, Describer describer, File fileUploadDir) {
		this.rootComponent = Objects.requireNonNull(rootComponent);
		this.beanDirectory = Objects.requireNonNull(beanDirectory);
		this.describer = Objects.requireNonNull(describer);
		this.uploadDirectory = fileUploadDir;
	}

	public BeanDirectory getBeanDirectory() {
		return beanDirectory;
	}

	public Describer getDescriber() {
		return describer;
	}

	public Object getRootComponent() {
		return rootComponent;
	}
	
	public File getUploadDirectory() {
		return uploadDirectory;
	}
}
