package org.oddjob.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.types.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * @oddjob.description Provides a Handler to Serve file content from the file system or
 * class path.
 * 
 * @oddjob.example Serve files from the same directory the configuration file is in.
 * 
 * {@oddjob.xml.resource org/oddjob/jetty/ResourceHandlerExample.xml}
 * 
 * @oddjob.example Serves files but also provides a list of welcome files.
 * 
 * {@oddjob.xml.resource org/oddjob/jetty/ResourceHandlerWelcomeFile.xml}
 * 
 * @author rob
 *
 */
public class ResourceHandlerType implements ValueFactory<Handler>{

	private static final Logger logger = 
			LoggerFactory.getLogger(ResourceHandlerType.class);
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The base directory or resource from where to serve content.
	 * @oddjob.required No, but pointless without one.
	 */
	private String base;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The type of resource, FILE or CLASSPATH.
	 * @oddjob.required No, defaults to FILE.
	 */
	private ResourceType resourceType;
	
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

	public interface JettyResourceType {
		
		Resource resourceFromString(String resource) throws IOException;
	}
	
	public enum ResourceType implements JettyResourceType {
		
		FILE {
			@Override
			public Resource resourceFromString(String resource) throws IOException {
				return Resource.newResource(resource);
			}
		},
		CLASSPATH {
			@Override
			public Resource resourceFromString(String resource) {
				return Resource.newClassPathResource(resource);
			}
		}
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
			 catch (IOException e) {
				 throw new ArooaConversionException(
						 "Faled converting " + base + " to a resource.", e);
			 }
			 
			 logger.debug("Setting base to " + base);
		 }

		return resourceHandler;
	}

	public String getBase() {
		return base;
	}

	@ArooaAttribute
	public void setBase(String base) {
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
		return 0;
	}

	/**
	 * @oddjob.property
	 * @oddjob.description Control memory mapped size. Set to -1 on windows because of
	 * <a href="https://wiki.eclipse.org/Jetty/Howto/Deal_with_Locked_Windows_Files">This issue</a>
	 * <p>
	 *     This property was deprecated, and then removed in version 10. The link above no
	 *     longer exists so we assume this has been fixed. This property now does nothing and
	 *     will be removed in Oddjob 1.8.
	 * </p>
	 *
	 * @oddjob.required No.
	 */
	@Deprecated
	public void setMinMemoryMappedContentLength(Integer minMemoryMappedFileSize) {
		logger.warn("minMemoryMappedContentLength no longer required. This property will be removed in future.");
	}
	
	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public String toString() {
		return getClass().getName() + ", base=" + base;
	}

}
