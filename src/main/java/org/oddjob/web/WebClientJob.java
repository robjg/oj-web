package org.oddjob.web;

import org.oddjob.Structural;
import org.oddjob.arooa.logging.LogLevel;
import org.oddjob.framework.extend.SimpleService;
import org.oddjob.jmx.RemoteDirectory;
import org.oddjob.jmx.RemoteDirectoryOwner;
import org.oddjob.jmx.SharedConstants;
import org.oddjob.jmx.client.*;
import org.oddjob.logging.ConsoleArchiver;
import org.oddjob.logging.LogArchiver;
import org.oddjob.logging.LogListener;
import org.oddjob.remote.RemoteConnection;
import org.oddjob.remote.RemoteConnector;
import org.oddjob.remote.RemoteException;
import org.oddjob.state.IsAnyState;
import org.oddjob.state.ServiceState;
import org.oddjob.structural.ChildHelper;
import org.oddjob.structural.StructuralListener;
import org.oddjob.web.client.ClientSessionImpl;
import org.oddjob.web.client.WebRemoteConnector;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @oddjob.description An Oddjob client that works over http.
 * 
 * @author Rob Gordon
 */
public class WebClientJob extends SimpleService
implements Structural, LogArchiver, ConsoleArchiver, RemoteDirectoryOwner {

	protected enum WhyStop {
		STOP_REQUEST,
		SERVER_STOPPED,
		HEARTBEAT_FAILURE
	}

	public static final long DEFAULT_LOG_POLLING_INTERVAL = 5000;
	
	/** The log poller thread */
	private RemoteLogPoller logPoller;
	
	/** Child helper */
	private final ChildHelper<Object> childHelper = new ChildHelper<>(this);
	
	/** The client session */
	private ClientSession clientSession;
		
	/** View of the main server bean. */
	private ServerView serverView;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The maximum number of log lines to retrieve for any
	 * component.
	 * @oddjob.required No.
	 */
	private int maxLoggerLines = LogArchiver.MAX_HISTORY;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The maximum number of console lines to retrieve for any
	 * component.
	 * @oddjob.required No.
	 */
	private int maxConsoleLines = LogArchiver.MAX_HISTORY;
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The number of milliseconds between polling for new
	 * log events. Defaults to 5.
	 * @oddjob.required No.
	 */
	private long logPollingInterval = 5000;

	/**
	 * @oddjob.property
	 * @oddjob.description The heart beat interval, in milliseconds.
	 * @oddjob.required Not, defaults to 5 seconds.
	 */
	private long heartbeat = 5000;

	private String host;

	private int port;

	/** The notification processor. */
	private volatile ScheduledExecutorService notificationProcessor;

	private RemoteConnector cntor;

	/**
	 * @oddjob.property
	 * @oddjob.description Additional handler factories that allow
	 * any interface to be invoked from a remote Oddjob.
	 *
	 * @oddjob.required No.
	 */
	private HandlerFactoryProvider handlerFactories;

	/* (non-Javadoc)
	 * @see org.oddjob.logging.LogArchiver#addLogListener(org.oddjob.logging.LogListener, java.lang.String, org.oddjob.logging.LogLevel, long, long)
	 */
	@Override
	public void addLogListener(LogListener l, Object component, LogLevel level,
			long last, int history) {
		stateHandler().assertAlive();

		final RemoteLogPoller logPoller = Optional.ofNullable(this.logPoller)
				.orElseThrow(() -> new NullPointerException("logPoller not available"));

		logPoller.addLogListener(l, component, level, last, history);
		// force poller to poll.
		synchronized (logPoller) {
			logPoller.notifyAll();
		}
	}

	/* (non-Javadoc)
	 * @see org.oddjob.logging.LogArchiver#removeLogListener(org.oddjob.logging.LogListener)
	 */
	@Override
	public void removeLogListener(LogListener l, Object component) {
		if (logPoller == null) {
			// must have been shut down.
			return;
		}
		logPoller.removeLogListener(l, component);
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.logging.ConsoleArchiver#addConsoleListener(org.oddjob.logging.LogListener, java.lang.Object, long, int)
	 */
	@Override
	public void addConsoleListener(LogListener l, Object component, long last,
			int max) {
		stateHandler().assertAlive();
		
		if (logPoller == null) {
			throw new NullPointerException("logPoller not available");
		}
		logPoller.addConsoleListener(l, component, last, max);
		// force main thread to poll.
		synchronized (this) {
			notifyAll();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.logging.ConsoleArchiver#removeConsoleListener(org.oddjob.logging.LogListener, java.lang.Object)
	 */
	@Override
	public void removeConsoleListener(LogListener l, Object component) {
		if (logPoller == null) {
			// must have been shut down.
			return;
		}
		logPoller.removeConsoleListener(l, component);
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.logging.ConsoleArchiver#consoleIdFor(java.lang.Object)
	 */
	@Override
	public String consoleIdFor(Object component) {
		return logPoller.consoleIdFor(component);
	}

	@Override
	public void onInitialised() {
		if (maxConsoleLines == 0) {
			maxConsoleLines = LogArchiver.MAX_HISTORY;
		}
		if (maxLoggerLines == 0) {
			maxLoggerLines = LogArchiver.MAX_HISTORY;
		}
		if (logPollingInterval == 0) {
			logPollingInterval = DEFAULT_LOG_POLLING_INTERVAL;
		}
	}

	@Override
	protected void onStart() throws Throwable {

		this.cntor = WebRemoteConnector.connect(host, port);

		notificationProcessor = Executors.newSingleThreadScheduledExecutor();

		doStart(cntor.getConnection(), notificationProcessor);
	}

	/**
	 *
	 */
	protected void doStart(RemoteConnection mbsc,
						   ScheduledExecutorService notificationProcessor) {

		ClientInterfaceManagerFactory managerFactory =
				new ClientInterfaceManagerFactoryBuilder()
						.addFactories(SharedConstants.DEFAULT_CLIENT_HANDLER_FACTORIES)
						.addFactories(new ResourceFactoryProvider(getArooaSession())
								.getHandlerFactories())
						.addFromProvider(handlerFactories)
						.build();

		clientSession = new ClientSessionImpl(
				mbsc,
				notificationProcessor,
				managerFactory,
				getArooaSession(),
				logger());
		
		Object serverMain = clientSession.create(0L);
		
		if (serverMain == null) {
			throw new NullPointerException("No Oddjob MBean found.");
		}
		
		serverView = new ServerView(serverMain);
				
		this.logPoller = new RemoteLogPoller(serverMain, 
				maxConsoleLines, maxLoggerLines);
		
		serverView.startStructural(childHelper);

		notificationProcessor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					serverView.noop();
				} catch (RuntimeException e) {
					try {
						doStop(WhyStop.HEARTBEAT_FAILURE, e);
					} catch (Exception e1) {
						logger().error("Failed to stop.", e1);
					}
				}
			}
			@Override
			public String toString() {
				return "Heartbeat";
			}
		}, getHeartbeat(), getHeartbeat(), TimeUnit.MILLISECONDS);
		
		logPoller.setLogPollingInterval(logPollingInterval);
		
		Thread t = new Thread(logPoller);
		t.start();
	}

	@Override
	protected void onStop() {
		doStop(WhyStop.STOP_REQUEST, null);
	}

	protected void doStop(final WhyStop why, final Exception cause) {

		// There is a small possibility that the SERVER_STOPPED and
		// HEARTBEAT_FAIURE happen simultaneously.
		ExecutorService notificationProcessor;
		synchronized (this) {
			notificationProcessor = this.notificationProcessor;
			if (notificationProcessor == null) {
				return;
			}
			this.notificationProcessor = null;
		}
		notificationProcessor.shutdownNow();

		onStop(why);

		if (why == WhyStop.STOP_REQUEST && cntor != null) {
			try {
				cntor.close();
			} catch (RemoteException e) {
				logger().debug("Failed to close connection: " + e);
			}
		}
		cntor = null;

		stateHandler().waitToWhen(new IsAnyState(), () -> {
			switch (why) {
				case HEARTBEAT_FAILURE:
					getStateChanger().setStateException(cause);
					logger().error(
							"Client stopped because of heartbeat Failure.",
							cause);
					break;
				case SERVER_STOPPED:
					getStateChanger().setStateException(
							new Exception("Server Stopped."));
					logger().info("Client stopped because server Stopped.");
					break;
				default:
					getStateChanger().setState(ServiceState.STOPPED);
					logger().debug(
							"Client stopped because stop was requested.");
			}
		});
	}


	
	protected void onStop(final WhyStop why) {

		logPoller.stop();
		
		// if not destroyed by remote peer
		if (why == WhyStop.STOP_REQUEST) {
			clientSession.destroy(serverView.getProxy());
		}		
		
		childHelper.removeAllChildren();
		
		clientSession.destroyAll();
		
		logPoller = null;
	}
		
	@Override
	public RemoteDirectory provideBeanDirectory() {
		if (serverView == null) {
			return null;
		}
		return serverView.provideBeanDirectory();
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.Structural#addStructuralListener(org.oddjob.structural.StructuralListener)
	 */
	@Override
	public void addStructuralListener(StructuralListener listener) {
		childHelper.addStructuralListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.Structural#removeStructuralListener(org.oddjob.structural.StructuralListener)
	 */
	@Override
	public void removeStructuralListener(StructuralListener listener) {
		childHelper.removeStructuralListener(listener);
	}

	public long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(long heartbeat) {
		this.heartbeat = heartbeat;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxConsoleLines() {
		return maxConsoleLines;
	}
	
	public void setMaxConsoleLines(int maxConsoleLines) {
		this.maxConsoleLines = maxConsoleLines;
	}
	
	public int getMaxLoggerLines() {
		return maxLoggerLines;
	}
	
	public void setMaxLoggerLines(int maxLoggerLines) {
		this.maxLoggerLines = maxLoggerLines;
	}
	
	public long getLogPollingInterval() {
		return logPollingInterval;
	}
	
	public void setLogPollingInterval(long logPollingInterval) {
		this.logPollingInterval = logPollingInterval;
	}

	public HandlerFactoryProvider getHandlerFactories() {
		return handlerFactories;
	}

	public void setHandlerFactories(HandlerFactoryProvider handlerFactories) {
		this.handlerFactories = handlerFactories;
	}
}
