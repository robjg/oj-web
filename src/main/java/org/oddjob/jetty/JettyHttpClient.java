package org.oddjob.jetty;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

public class JettyHttpClient implements Callable<Integer> {

	private volatile String url;
	
	private volatile HttpMethod httpMethod;
	
	private volatile int status;
	
	private volatile String content;
	
	private volatile Properties properties;
	
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
		
		ContentResponse response = request.send();
		
		status = response.getStatus();
		
		content = response.getContentAsString();
		
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
}
