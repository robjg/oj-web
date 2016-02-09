package org.oddjob.jetty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Echo a JSON representation of the HTTP Servlet Request. Useful for debugging and 
 * diagnostics.
 * 
 * @author rob
 *
 */
public class EchoRequestHandler extends AbstractHandler {

	private static final Logger logger = Logger.getLogger(EchoRequestHandler.class);
	
	/** The parameter map will use up the content so you either see either/or.
	 */
	private boolean parameterMap;
	
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
				
		response.setContentType("application/json; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		try (PrintWriter out = response.getWriter()) {

			Gson gson = new GsonBuilder().serializeNulls().create();
			String json = gson.toJson(createRequestBean(request));  
			
			out.println(json);
			
			logger.info("Echo Response: " + json);
        } 
		
		baseRequest.setHandled(true);
	}

	protected EchoRequestBean createRequestBean(HttpServletRequest request) throws IOException {
		
		EchoRequestBean bean = new EchoRequestBean();

//		Enumeration<String> attributeNames = request.getAttributeNames();
		
		bean.setMethod(request.getMethod());
		bean.setProtocol(request.getProtocol());
		bean.setScheme(request.getScheme());
		
		bean.setAuthType(request.getAuthType());
		Principal principal = request.getUserPrincipal();
		bean.setUserPrincipalName(principal == null ? 
				null : principal.getName());
		
		bean.setContextPath(request.getContextPath());
		bean.setQueryString(request.getQueryString());
		bean.setPathInfo(request.getPathInfo());
		bean.setPathTranslated(request.getPathTranslated());
		
		Map<String, String[]> headerMap = new LinkedHashMap<>();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			List<String> headers = Collections.list(request.getHeaders(headerName));
			headerMap.put(headerName, headers.toArray(new String[headers.size()]));
		}		
		bean.setHeaderMap(headerMap);
				
		bean.setCharacterEncoding(request.getCharacterEncoding());
		bean.setContentLength(request.getContentLengthLong());
		bean.setContentType(request.getContentType());
		
		if (parameterMap || request.getContentLengthLong() == 0L) {
			bean.setParameterMap(request.getParameterMap());
		}
		else {
			try (InputStream input = request.getInputStream(); 
					ByteArrayOutputStream output = new ByteArrayOutputStream()) {
				byte[] buff = new byte[1024];
				for (int i = input.read(buff); i > 0; i = input.read(buff)) {
					output.write(buff, 0, i);
				};
				bean.setContent(new String(output.toByteArray()));
			} 		
		}
				
		return bean;
	}

	public boolean isParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(boolean parameterMap) {
		this.parameterMap = parameterMap;
	}		
}