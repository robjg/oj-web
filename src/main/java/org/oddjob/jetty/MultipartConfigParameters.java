package org.oddjob.jetty;

import javax.servlet.MultipartConfigElement;

/**
 * Bean to provide configuration for an {@link javax.servlet.MultipartConfigElement}.
 *
 * @see OddjobRestHandler
 */
public class MultipartConfigParameters {

	private volatile String location;
	
	private volatile long maxFileSize = -1L;
	
	private volatile long maxRequestSize = -1L;
	
	private volatile int fileSizeThreshold = 0;
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public long getMaxRequestSize() {
		return maxRequestSize;
	}

	public void setMaxRequestSize(long maxRequestSizeK) {
		this.maxRequestSize = maxRequestSizeK;
	}

	public int getFileSizeThreshold() {
		return fileSizeThreshold;
	}

	public void setFileSizeThreshold(int fileSizeThresholdK) {
		this.fileSizeThreshold = fileSizeThresholdK;
	}
	    
	public MultipartConfigElement toMultipartConfigElement() {

		return new MultipartConfigElement(location,
				maxFileSize, maxRequestSize, fileSizeThreshold);
	}

}
