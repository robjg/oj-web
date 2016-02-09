package org.oddjob.jetty;

import java.net.MalformedURLException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.types.ValueFactory;

/**
 * @oddjob.description Serve file content.
 * 
 * @author rob
 *
 */
public class ResourceHandlerType implements ValueFactory<Handler>{

	private static final Logger logger = 
			Logger.getLogger(ResourceHandlerType.class);
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The base directory from where to serve content.
	 * @oddjob.required No, but pointless without one.
	 */
	private String base;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The type of resource, FILE or CLASSPATH.
	 * @oddjob.required No, defaults to FILE.
	 */
	private JettyResourceType resourceType;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description List directories or not.
	 * @oddjob.required No, defaults to false.
	 */
	private boolean directoriesListed;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description List of welcome files to serve.
	 * @oddjob.required No.
	 */
	private String[] welcomeFiles;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description Control memory mapped size. Set to -1 on windows because of
	 * <a href="https://wiki.eclipse.org/Jetty/Howto/Deal_with_Locked_Windows_Files">This issue</a>
	 * @oddjob.required No.
	 */
	private Integer minMemoryMappedContentLength;
	
	public interface JettyResourceType {
		
		Resource resourceFromString(String resource) throws MalformedURLException;
	}
	
	public enum ResourceType implements JettyResourceType {
		
		FILE {
			@Override
			public Resource resourceFromString(String resource) throws MalformedURLException {
				return Resource.newResource(resource);
			}
		},
		CLASSPATH {
			@Override
			public Resource resourceFromString(String resource) throws MalformedURLException {
				return Resource.newClassPathResource(resource);
			}
		}
		;
	}
	
	@Override
	public Handler toValue() throws ArooaConversionException {

		 ResourceHandler resourceHandler = new ResourceHandler();
		 
		 resourceHandler.setDirectoriesListed(directoriesListed);

		 if (welcomeFiles != null) {
			 resourceHandler.setWelcomeFiles(welcomeFiles);
			 logger.debug("Setting welcome files to " + Arrays.toString(welcomeFiles));
		 }
		 
		 if (base != null) {
			 JettyResourceType resourceType = this.resourceType;
			 if (resourceType == null) {
				 resourceType = ResourceType.FILE;
			 }
			 
			 try {
				 resourceHandler.setBaseResource(resourceType.resourceFromString(base));
			 }
			 catch (MalformedURLException e) {
				 throw new ArooaConversionException(
						 "Faled converting " + base + " to a resource.", e);
			 }
			 
			 logger.debug("Setting base directory to " + base);
		 }
		 
		 if (minMemoryMappedContentLength != null) {
			 resourceHandler.setMinMemoryMappedContentLength(minMemoryMappedContentLength);
		 }
		 
		return resourceHandler;
	}

	public String getBaseDir() {
		return base;
	}

	@ArooaAttribute
	public void setBaseDir(String base) {
		this.base = base;
	}

	public boolean isDirectoriesListed() {
		return directoriesListed;
	}

	public void setDirectoriesListed(boolean directoriesListed) {
		this.directoriesListed = directoriesListed;
	}

	public String[] getWelcomeFiles() {
		return welcomeFiles;
	}

	public void setWelcomeFiles(String[] welcomeFiles) {
		this.welcomeFiles = welcomeFiles;
	}
	
	public Integer getMinMemoryMappedContentLength() {
		return minMemoryMappedContentLength;
	}

	public void setMinMemoryMappedContentLength(Integer minMemoryMappedFileSize) {
		this.minMemoryMappedContentLength = minMemoryMappedFileSize;
	}
	
	public JettyResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(JettyResourceType resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public String toString() {
		return getClass().getName() + ", base=" + base;
	}

}
