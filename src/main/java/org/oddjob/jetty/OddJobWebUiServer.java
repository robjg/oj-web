package org.oddjob.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.oddjob.FailedToStopException;
import org.oddjob.OddjobException;
import org.oddjob.Stoppable;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.framework.Service;
import org.oddjob.jmx.RemoteIdMappings;
import org.oddjob.jmx.ServerStrategy;
import org.oddjob.jmx.server.JmxServer;
import org.oddjob.jmx.server.ServerSide;
import org.oddjob.jmx.server.ServerSideBuilder;
import org.oddjob.web.WebServerHandlerJmx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import java.io.File;
import java.util.Optional;

/**
 * @oddjob.description Provide an Oddjob Web UI Server.
 *
 *
 * @author rob
 *
 */
public class OddJobWebUiServer implements Service, ArooaSessionAware {

	private static final Logger logger = LoggerFactory.getLogger(OddJobWebUiServer.class);

	public static final String WEBAPP_RESOURCE = "dist";

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
	 * @oddjob.description The root component to expose.
	 * @oddjob.required No, but a Jmx Server must be provided instead.
	 */
	private Object root;

	/**
	 * @oddjob.property
	 * @oddjob.description An Oddjob JMX Server.
	 * @oddjob.required Yes, for the time being. Will default soon.
	 */
	private JmxServer jmxServer;

	/**
	 * @oddjob.property
	 * @oddjob.description The class path to the html files for oddjob web.
	 * @oddjob.required No. Defaults to /dist on the class path.
	 */
	private volatile String webappResource;

	/**
	 * @oddjob.property
	 * @oddjob.description The directory for the html files for oddjob web. Mainly used for
	 * development to save stopping and starting Jetty.
	 * @oddjob.required No.
	 */
	private volatile File webappDir;

	/**
	 * @oddjob.property
	 * @oddjob.description Set parameters for MultiPartConfig so that file upload from a form works.
	 * @oddjob.required No. Defaults are used.
	 */
	private volatile MultipartConfigParameters multiPartConfig;

	/**
	 * @oddjob.property
	 * @oddjob.description Upload directory. Required for an action form that specifies a file.
	 * @oddjob.required No. Defaults tmp dir.
	 */
	private volatile File uploadDirectory;

	/**
	 * @oddjob.property
	 * @oddjob.description Is cross-origin content allowed?
	 * @oddjob.required No. Default to false.
	 */
	private volatile boolean allowCrossOrigin;

	private ArooaSession session;

	/** The Jetty Server instance. */
	private volatile Server server;

	private volatile ClassLoader classLoader;

	private Stoppable jmxServerStop;

	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}

	@Override
	public void start() throws Exception {

		if (server != null) {
			throw new IllegalStateException("Server already started.");
		}

		ClassLoader classLoader = Optional.ofNullable(this.classLoader)
				.orElse(Thread.currentThread().getContextClassLoader());

		server = new Server(port);

		String webappResource = Optional.ofNullable(this.webappResource)
				.orElse(WEBAPP_RESOURCE);

		JmxServer jmxServer = this.jmxServer;
		this.jmxServerStop = () -> {};

		if (jmxServer == null) {
			StoppableJmxServer stoppableJmxServer = createDefaultServer();
			this.jmxServerStop = stoppableJmxServer;
			jmxServer = stoppableJmxServer;
		}

		Handler handler = new HandlerList(
				resourceHandler(webappResource),
				createJmxHandler(jmxServer, classLoader));

		server.setHandler(handler);

		try {
			server.start();
			
			port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
		}
		catch (Exception e) {
			jmxServerStop.stop();
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
		finally {
			jmxServerStop.stop();
		}
	}

	protected Handler createJmxHandler(JmxServer jmxServer, ClassLoader classLoader) {

		WebServerHandlerJmx jmxHandler = new WebServerHandlerJmx();
		jmxHandler.setClassLoader(classLoader);
		jmxHandler.setJmxServer(jmxServer);
		jmxHandler.setArooaSession(this.session);
		jmxHandler.setAllowCrossOrigin(this.allowCrossOrigin);
		jmxHandler.setUploadDirectory(this.uploadDirectory);
		jmxHandler.setMultiPartConfig(this.multiPartConfig);

		return jmxHandler.toValue();
	}

	interface StoppableJmxServer extends JmxServer, Stoppable {}

	protected StoppableJmxServer createDefaultServer() throws JMException {

		Object root = this.root;
		if (root == null) {
			throw new OddjobException("No root node.");
		}

		logger.info("Creating default JMX server with root [{}]", root);
		ServerStrategy serverStrategy = ServerStrategy.strategyForPlatform();

		MBeanServer server = serverStrategy.findServer();

		ServerSide factory = ServerSideBuilder.withSession(session)
				.buildWith(server,
						serverStrategy.serverIdText(),
						root);

		return new StoppableJmxServer() {
			@Override
			public RemoteIdMappings getRemoteIdMappings() {
				return factory.getRemoteIdMappings();
			}

			@Override
			public MBeanServerConnection getServerConnection() {
				return factory.getServerConnection();
			}

			@Override
			public void stop() {
				factory.close();
			}
		};
	}

	protected Handler resourceHandler(String webappResource) {

		ResourceHandler resourceHandler = new ResourceHandler();

		Resource base;
		File webappDir = this.webappDir;
		if (webappDir == null) {
			base = Resource.newClassPathResource(webappResource);
			logger.info("Creating resource handler with Classpath resource {}", webappResource);
		}
		else {
			base = Resource.newResource(webappDir);
			logger.info("Creating resource handler with Dir resource {}", webappDir);
		}

		resourceHandler.setBaseResource(base);
		resourceHandler.setWelcomeFiles(new String[]{"index.html"});

		return resourceHandler;
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

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Object getRoot() {
		return root;
	}

	@ArooaAttribute
	public void setRoot(Object root) {
		this.root = root;
	}

	public JmxServer getJmxServer() {
		return jmxServer;
	}

	@ArooaAttribute
	public void setJmxServer(JmxServer jmxServer) {
		this.jmxServer = jmxServer;
	}

	public String getWebappResource() {
		return webappResource;
	}

	public void setWebappResource(String webappResource) {
		this.webappResource = webappResource;
	}

	public File getWebappDir() {
		return webappDir;
	}

	public void setWebappDir(File webappDir) {
		this.webappDir = webappDir;
	}

	public boolean isAllowCrossOrigin() {
		return allowCrossOrigin;
	}

	public void setAllowCrossOrigin(boolean allowCrossOrigin) {
		this.allowCrossOrigin = allowCrossOrigin;
	}

	public MultipartConfigParameters getMultiPartConfig() {
		return multiPartConfig;
	}

	public void setMultiPartConfig(MultipartConfigParameters multiPartConfig) {
		this.multiPartConfig = multiPartConfig;
	}

	public File getUploadDirectory() {
		return uploadDirectory;
	}

	public void setUploadDirectory(File uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
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
