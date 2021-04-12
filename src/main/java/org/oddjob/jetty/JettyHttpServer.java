package org.oddjob.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.oddjob.FailedToStopException;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.framework.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @oddjob.description An HTTP server.
 * <p>
 * This is a wrapper around the Jetty <a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/server/Server.html">Server</a>.
 * The {@code handlers} property must be used to configure the server to do anything useful. By default 
 * a simple welcome message is returned for all requests.
 * 
 * <p>
 * Common handlers to use are:
 * <ul>
 * <li>{@link ResourceHandlerType}</li>
 * <li>{@link OddjobWebHandler}</li>
 * </ul>
 * 
 * @oddjob.example
 * 
 * The simplest web server without any handlers.
 * 
 * {@oddjob.xml.resource org/oddjob/jetty/DefaultServerExample.xml}
 *
 * @oddjob.example
 *
 * Using Basic Authentication.
 *
 * {@oddjob.xml.resource org/oddjob/jetty/BasicAuthServer.xml}
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
	 * @oddjob.description The Jetty Handler. To provide a list of handlers that will be tried in order
	 * use {@link HandlerListType}.
	 * @oddjob.required No, but pointless if missing.
	 */
	private volatile Handler handler;

	/**
	 * @oddjob.property
	 * @oddjob.description Provide Beans directly to the Jetty Server for management by Jetty.
	 * Currently untested.
	 * @oddjob.required No.
	 */
	private final List<Object> beans = new CopyOnWriteArrayList<>();


	/** The Jetty Server instance. */
	private volatile Server server;

	@Override
	public void start() throws Exception {

		if (server != null) {
			throw new IllegalStateException("Server already started.");
		}

		server = new Server(port);

		beans.forEach(server::addBean);

		try {

			Handler handler = Optional.ofNullable(this.handler)
					.<Handler>map(h -> new HandlerList(h, new DefaultHandler()))
					.orElseGet(WelcomeHandler::new);

			server.setHandler(handler);

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
	
	public Handler getHandler() {
		return handler;
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Object getBeans(int index) {

		return beans.get(index);
	}

	public void setBeans(int index, Object bean) {

		new ListSetterHelper<>(beans).set(index, bean);
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
