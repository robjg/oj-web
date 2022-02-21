package org.oddjob.jetty;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

/**
 * A very simple handler that just displays a welcome. Based on Jetty's 
 * <a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/server/handler/DefaultHandler.html">DefaultHandler</a>.
 * 
 * @author rob
 *
 */
public class WelcomeHandler extends AbstractHandler {

    private final long _faviconModified=(System.currentTimeMillis()/1000)*1000L;
    private final byte[] _favicon;
	
	public WelcomeHandler() {
        try {
            URL fav = this.getClass().getClassLoader().getResource(
            		"org/oddjob/webapp/favicon.ico");

            if (fav == null) {
            	throw new NullPointerException("Oddjob's favicon.ico not found.");
            }

            Resource r = Resource.newResource(fav);
            _favicon=IO.readBytes(r.getInputStream());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed reading Oddjob's favicon.", e);
        }
	}
	
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

        baseRequest.setHandled(true);

        String method=request.getMethod();

        // little cheat for common request - copied straight from Jetty's DefaultHandler.
        if (_favicon!=null && HttpMethod.GET.is(method) 
        		&& request.getRequestURI().equals("/favicon.ico")) {
            if (request.getDateHeader(
            		HttpHeader.IF_MODIFIED_SINCE.toString())==_faviconModified) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
            else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("image/x-icon");
                response.setContentLength(_favicon.length);
                response.setDateHeader(HttpHeader.LAST_MODIFIED.toString(), 
                		_faviconModified);
                response.setHeader(HttpHeader.CACHE_CONTROL.toString(),
                		"max-age=360000,public");
                response.getOutputStream().write(_favicon);
            }
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MimeTypes.Type.TEXT_HTML.toString());

		PrintWriter out = response.getWriter();

		out.print("<html><head><title>Oddjob Welcome</title><body>");
		out.print("<h2>Welcome to Oddjob's Embedded HTTP Server (Jetty)</h2>");
		out.print("</body></html>");
		
		out.flush();
		
		baseRequest.setHandled(true);
	}

}
