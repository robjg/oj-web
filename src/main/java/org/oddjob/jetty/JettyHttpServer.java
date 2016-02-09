package org.oddjob.jetty;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.oddjob.FailedToStopException;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.framework.Service;

/**
 * @oddjob.description An HTTP server.
 * <p>
 * This is a wrapper around the Jetty <a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/server/Server.html">Server</a>.
 * The {@code handlers} property must be used to configure the server to do anything useful. By default only Jetty's
 * <a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/server/handler/DefaultHandler.html">DefaultHandler</a>
 * which returns status {@code 404} for all requests.
 * <p>
 * Common handlers to use are:
 * <ul>
 * <li>{@link ResourceHandlerType}</li>
 * <li>{@link OddjobWebHandler}</li>
 * </ul>
 * 
 * @oddjob.example
 * 
 * Provide an Oddjob web client.
 * 
 * {@oddjob.xml.resource org/oddjob/jetty/OddjobWeb.xml}
 * 
 * 
 * @author rob
 *
 */
public class JettyHttpServer implements Service {

	/** 
	 * @oddjob.property
	 * @oddjob.description The name of service. Can be any text.
	 * @oddjob.required No.
	 */
	private volatile String name;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The port number the server listens on.
	 * @oddjob.required No, uses a random available port.
	 */
	private volatile int port;

	/** 
	 * @oddjob.property
	 * @oddjob.description List of Jetty Handlers.
	 * @oddjob.required No, but pointless if missing.
	 */
	private final List<Handler> handlers = new CopyOnWriteArrayList<>();
	
	/** The Jetty Server instance. */
	private volatile Server server;

	@Override
	public void start() throws Exception {

		if (server != null) {
			throw new IllegalStateException("Server already started.");
		}

		server = new Server(port);
		
		try {
			HandlerList handlerList = new HandlerList();
			
			handlerList.setHandlers(handlers.toArray(
					new Handler[handlers.size()]));
	
			handlerList.addHandler(new DefaultHandler());
			
			server.setHandler(handlerList);
			
			server.start();
			
			port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
		}
		catch (Exception e) {
			server = null;
			throw e;
		}
	}

	@Override
	public void stop() throws FailedToStopException {

		if (server == null) {
			throw new IllegalStateException("Server not started.");
		}

		try {
			server.stop();
			server.join();
			server = null;
		} catch (Exception e) {
			throw new FailedToStopException(this, e);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public Handler getHandlers(int index) {
		return handlers.get(index);
	}
	
	public void setHandlers(int index, Handler handler) {
		new ListSetterHelper<Handler>(handlers).set(index, handler);
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
