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

public class JettyHttpServer implements Service {

	private volatile String name;
	
	private volatile Server server;

	private volatile int port;

	private final List<Handler> handlers = new CopyOnWriteArrayList<>();
	
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
