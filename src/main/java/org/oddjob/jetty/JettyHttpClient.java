package org.oddjob.jetty;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

public class JettyHttpClient implements Callable<Integer> {

	private static final Logger logger = Logger.getLogger(JettyHttpClient.class);
	
	private volatile String name;
	
	private volatile String url;
	
	private volatile HttpMethod httpMethod;
	
	private volatile int status;
	
	private volatile String content;
	
	private volatile Properties properties;
	
	private volatile String contentType;
	
	@Override
	public Integer call() throws Exception {
		
		if (url == null) {
			throw new IllegalArgumentException("No URL");
		}
		
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		
		Request request = httpClient.newRequest(url);
		
		if (httpMethod == null) {
			httpMethod = HttpMethod.GET;
		}
		request.method(httpMethod);
		
		if (request.getScheme() == null) {
			request.scheme("http");
		}
		
		Properties properties = this.properties;
		if (properties != null) {
			for (String property : properties.stringPropertyNames()) {
				request.param(property, properties.getProperty(property));
			}
		}
		
		if (contentType != null) {
			request.header("Content-Type", contentType);
		}
				
		logger.info("Making " + request.getMethod() + " to " + request.getURI());
		
		ContentResponse response = request.send();
		
		status = response.getStatus();
		content = response.getContentAsString();
		
		if (HttpStatus.OK_200  == status) {
			logger.info("Response OK, response content length " + content.length());
		}
		else {
			logger.info("Response " + HttpStatus.getCode(status) + 
					", reason" + response.getReason());
		}
		
		httpClient.stop();
		
		return 0;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getContent() {
		return content;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public String toString() {
		if (name == null) {
			return getClass().getSimpleName();
		}
		else {
			return name;
		}
	}
}
