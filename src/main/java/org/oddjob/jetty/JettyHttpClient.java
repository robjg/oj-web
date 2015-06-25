package org.oddjob.jetty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.Fields;

public class JettyHttpClient implements Callable<Integer> {

	private static final Logger logger = Logger.getLogger(JettyHttpClient.class);
	
	private volatile String name;
	
	private volatile String url;
	
	private volatile RequestMethod method;
	
	private volatile int status;
	
	private volatile String content;
	
	private volatile Map<String, String> parameters;
	
	private volatile String contentType;
	
	private interface RequestStrategy {
		
		ContentResponse doRequest(HttpClient httpClient, RequestConfiguration self)
		throws ExecutionException, InterruptedException, TimeoutException;
	}
	
	private class RequestConfiguration {
		
		private final String url;
		
		private final Map<String, String> parameters;
		
		private final String content;
		
		private final String contentType;
		
		public RequestConfiguration() {

			url = JettyHttpClient.this.url;
			
			if (url == null) {
				throw new IllegalArgumentException("No URL");
			}
			
			if (JettyHttpClient.this.parameters == null) {
				parameters = null;
			}
			else {
				parameters = new LinkedHashMap<>(
						JettyHttpClient.this.parameters);
			}
			
			content = JettyHttpClient.this.content;
			contentType = JettyHttpClient.this.contentType;
		}
		
	}
	
	public enum RequestMethod implements RequestStrategy {
		
		GET {
			@Override
			public ContentResponse doRequest(HttpClient httpClient, RequestConfiguration self) 
			throws ExecutionException, InterruptedException, TimeoutException{
				
				Request request = httpClient.newRequest(self.url);

				if (self.parameters != null) {
					for (Map.Entry<String, String>  entry : self.parameters.entrySet()) {
						request.param(entry.getKey(), entry.getValue());
					}
				}
				
				return request.send();
			}
		},
		
		POST {
			@Override
			public ContentResponse doRequest(HttpClient httpClient, RequestConfiguration self) 
			throws InterruptedException, TimeoutException, ExecutionException {
				
				if (self.parameters == null) {
					
					Request request = httpClient.POST(self.url);
					request.content(new StringContentProvider(self.content), 
							self.contentType);
					
					return request.send();
				}
				else {
					
					Fields fields = new Fields();
					
					for (Map.Entry<String, String>  entry : self.parameters.entrySet()) {
						fields.put(entry.getKey(), entry.getValue());
					}
					
					return httpClient.FORM(self.url, fields);
				}
			}
		},
		
	}
	
	@Override
	public Integer call() throws Exception {
		
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		
		try {
			RequestConfiguration config = new RequestConfiguration();
			
			if (method == null) {
				method = RequestMethod.GET;
			}
					
			logger.info("Making " + method + " to " + config.url);
			
			ContentResponse response = method.doRequest(httpClient, config);
			
			status = response.getStatus();
			content = response.getContentAsString();
			
			if (HttpStatus.OK_200  == status) {
				logger.info("Response OK, response content length " + content.length());
				return 0;
			}
			else {
				logger.info("Response " + HttpStatus.getCode(status) + 
						", reason" + response.getReason());
				return 1;
			}
		}
		finally {
			httpClient.stop();
		}
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

	public void setContent(String content) {
		this.content = content;
	}
	
	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> properties) {
		this.parameters = properties;
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
