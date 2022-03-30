package org.oddjob.jetty;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @oddjob.description Execute an HTTP client request.
 * 
 * This is a very simple wrapper around Jetty's 
 * <a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/client/HttpClient.html">HTTPClient</a>.
 * It was developed to support testing of Oddjob's web service and as such it is quite
 * limited. It only supports PUT and GET requests and has no support for authentication.
 * 
 * @oddjob.example
 * 
 * Get the content of a URL using a parameter.
 * 
 * {@oddjob.xml.resource org/oddjob/jetty/ClientGetExample.xml}
 *
 * @oddjob.example
 *
 * Basic Authentication.
 *
 * {@oddjob.xml.resource org/oddjob/jetty/BasicAuthClient.xml}
 *
 * @author rob
 *
 */
public class JettyHttpClient implements Callable<Integer> {

	private static final Logger logger = LoggerFactory.getLogger(JettyHttpClient.class);
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The name of the job. Can be any text.
	 * @oddjob.required No.
	 */
	private volatile String name;
	
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The URL to connect to. Must be a full URL, e.g. http://www.google.com
	 * @oddjob.required Yes.
	 */
	private volatile String url;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The request method. GET/POST.
	 * @oddjob.required No defaults to GET.
	 */
	private volatile RequestMethod method;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The return status.
	 * @oddjob.required Read Only.
	 */
	private volatile int status;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The content retrieved or to send.
	 * @oddjob.required No.
	 */
	private volatile String requestBody;

	/**
	 * @oddjob.property
	 * @oddjob.description The content retrieved or to send.
	 * @oddjob.required No.
	 */
	private volatile String responseBody;

	/**
	 * @oddjob.property
	 * @oddjob.description Parameters.
	 * @oddjob.required No.
	 */
	private volatile Map<String, String> parameters;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The content type of a POST request. Useful for sending forms.
	 * @oddjob.required No.
	 */
	private volatile String contentType;

	/**
	 * @oddjob.property
	 * @oddjob.description Provide Username/Password for Basic Authentication.
	 * @oddjob.required No.
	 */
	private volatile UsernamePassword basicAuthentication;

	/**
	 * @oddjob.property
	 * @oddjob.description Provide SSL Configuration.
	 * @oddjob.required No.
	 */
	private volatile ClientSslProvider ssl;


	private interface RequestStrategy {
		
		ContentResponse doRequest(HttpClient httpClient, RequestConfiguration self)
		throws ExecutionException, InterruptedException, TimeoutException;
	}
	
	/**
	 * Collect all request parameters together.
	 */
	private class RequestConfiguration {
		
		private final String url;
		
		private final Map<String, String> parameters;
		
		private final String content;
		
		private final String contentType;

		private final URI uri;

		public RequestConfiguration() throws URISyntaxException {

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
			
			content = JettyHttpClient.this.requestBody;
			contentType = JettyHttpClient.this.contentType;

			this.uri = new URI(url);
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

		HttpClient httpClient = Optional.ofNullable(this.ssl)
				.map(ssl -> new HttpClient(ssl.provideClientSsl()))
				.orElseGet(() -> new HttpClient());

		httpClient.start();

		try {
			RequestConfiguration config = new RequestConfiguration();

			Optional.ofNullable(this.basicAuthentication)
					.ifPresent(up ->
							httpClient.getAuthenticationStore().addAuthentication(
									new BasicAuthentication(
											config.uri,
											Authentication.ANY_REALM,
											up.username,
											up.password)));

			if (method == null) {
				method = RequestMethod.GET;
			}
					
			logger.info("Making " + method + " request to " + config.url);
			
			ContentResponse response = method.doRequest(httpClient, config);
			
			status = response.getStatus();
			responseBody = response.getContentAsString();
			
			if (HttpStatus.OK_200  == status) {
				logger.info("Response OK, response content length " + responseBody.length());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return responseBody;
	}

	public void setContent(String content) {
		this.requestBody = content;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseBody() {
		return responseBody;
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

	public UsernamePassword getBasicAuthentication() {
		return basicAuthentication;
	}

	public void setBasicAuthentication(UsernamePassword basicAuthentication) {
		this.basicAuthentication = basicAuthentication;
	}

	public ClientSslProvider getSsl() {
		return ssl;
	}

	public void setSsl(ClientSslProvider ssl) {
		this.ssl = ssl;
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

	public static class UsernamePassword {

		private volatile String username;

		private volatile String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
