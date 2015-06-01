package org.oddjob.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
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

public class EchoRequestHandler extends AbstractHandler {

	private static final Logger logger = Logger.getLogger(EchoRequestHandler.class);
	
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
		
		bean.setAuthType(request.getAuthType());
		bean.setCharacterEncoding(request.getCharacterEncoding());
		bean.setContentLength(request.getContentLengthLong());
		bean.setContextPath(request.getContextPath());
		
		Map<String, String[]> headerMap = new LinkedHashMap<>();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			List<String> headers = Collections.list(request.getHeaders(headerName));
			headerMap.put(headerName, headers.toArray(new String[headers.size()]));
		}		
		bean.setHeaderMap(headerMap);
		
		bean.setMethod(request.getMethod());
		bean.setParameterMap(request.getParameterMap());
		bean.setContentType(request.getContentType());
		
		
		try (Reader input = request.getReader(); StringWriter writer = new StringWriter()) {
			for (char[] buff = new char[1024]; input.read(buff) > 0; ) {
				writer.write(buff);
			};
			bean.setContent(writer.toString());
		} 		
		
		return bean;
	}		
}