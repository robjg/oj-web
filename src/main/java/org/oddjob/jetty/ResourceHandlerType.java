package org.oddjob.jetty;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.types.ValueFactory;

public class ResourceHandlerType implements ValueFactory<Handler>{

	private static final Logger logger = 
			Logger.getLogger(ResourceHandlerType.class);
	
	private File baseDir;
	
	private boolean directoriesListed;
	
	private String[] welcomeFiles;
	
	@Override
	public Handler toValue() throws ArooaConversionException {

		 ResourceHandler resourceHandler = new ResourceHandler();
		 
		 resourceHandler.setDirectoriesListed(directoriesListed);
		 
		 if (welcomeFiles != null) {
			 resourceHandler.setWelcomeFiles(welcomeFiles);
			 logger.debug("Setting welcome files to [" + Arrays.toString(welcomeFiles));
		 }
		 
		 if (baseDir != null) {
			 if (!baseDir.exists()) {
				 throw new ArooaConversionException(baseDir + " does not exist.");
			 }
			 if (!baseDir.isDirectory()) {
				 throw new ArooaConversionException(baseDir + " is not a directory.");				 
			 }
			 resourceHandler.setBaseResource(Resource.newResource(baseDir));
			 logger.debug("Setting base directory to " + baseDir);
		 }
		 
		return resourceHandler;
	}

	public File getBaseDir() {
		return baseDir;
	}

	@ArooaAttribute
	public void setBaseDir(File base) {
		this.baseDir = base;
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
	
	@Override
	public String toString() {
		return getClass().getName() + ", base=" + baseDir;
	}
}
