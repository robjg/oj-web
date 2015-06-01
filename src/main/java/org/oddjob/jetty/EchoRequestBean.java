package org.oddjob.jetty;

import java.util.Map;

public class EchoRequestBean {
	
	private String authType;
	
	private String characterEncoding; 
	
	private long contentLength;
	
	private String contentType; 
	
	private String contextPath; 
	
	private String method;
	
	private Map<String, String[]> parameterMap;

	private Map<String, String[]> headerMap;
	
	private String content;
	
	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap = parameterMap;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, String[]> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, String[]> headers) {
		this.headerMap = headers;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	} 
	
}