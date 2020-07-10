package org.oddjob.web.client;

import org.oddjob.arooa.utils.Pair;
import org.oddjob.jmx.RemoteOperation;
import org.oddjob.jmx.Utils;
import org.oddjob.jmx.client.ClientSession;
import org.oddjob.jmx.client.ClientSideToolkit;
import org.oddjob.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link ClientSideToolkit}.
 * 
 * @author rob
 *
 */
class ClientSideToolkitImpl implements ClientSideToolkit {
	private static final Logger logger = LoggerFactory.getLogger(ClientSideToolkitImpl.class);

	private final static int ACTIVE = 0;

	private final static int DESTROYED = 3;
	
	private volatile int phase = ACTIVE;

	private final ClientSessionImpl clientSession;

	private final long remoteId;

	private final Set<Pair<NotificationType<?>, NotificationListener<?>>> listeners = ConcurrentHashMap.newKeySet();

	public ClientSideToolkitImpl(long remoteId,
                                 ClientSessionImpl clientSession) {

		this.clientSession = Objects.requireNonNull(clientSession);
		this.remoteId = remoteId;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T invoke(RemoteOperation<T> remote, Object... args) throws Throwable {

		OperationType<T> operationType;
		if (remote instanceof HasOperationType) {
			operationType = ((HasOperationType<T>) remote).getOperationType();
		}
		else {
			throw new IllegalArgumentException("No operation type" + remote);
		}

		Objects.requireNonNull(remote);

		Object[] exported = Utils.export(args);

		Object result = clientSession.getRemoteConnection().invoke(
					remoteId,
					operationType,
					exported);

		return (T) Utils.importResolve(result, clientSession);

	}

	@Override
	public <T> void registerNotificationListener(NotificationType<T> eventType,
												 NotificationListener<T> notificationListener) {
		try {
			clientSession.getRemoteConnection().addNotificationListener(remoteId, eventType, notificationListener);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		listeners.add(Pair.of(eventType, notificationListener));
	}

	@Override
	public <T> void removeNotificationListener(NotificationType<T> eventType,
			NotificationListener<T> notificationListener) {
		try {
			clientSession.getRemoteConnection().removeNotificationListener(remoteId, eventType, notificationListener);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		listeners.remove(Pair.of(eventType, notificationListener));
	}
	
	public ClientSession getClientSession() {
		return clientSession;
	}
	
	/**
	 * Destroy this node. Clean up resources, remove remote connections.
	 */
	void destroy() {
		phase = DESTROYED;
		// beware the order here. 
		// notifications removed first
		for (Pair<NotificationType<?>, NotificationListener<?>> listener : listeners) {
			try {
				// will fail if destroy is due to the remote node being removed.
				removeListener(listener);
			} catch (Exception e) {
				logger.debug("Client destroy.", e);
			}
		}
		logger.debug("Destroyed client for [" + toString() + "]");
	}

	@SuppressWarnings("unchecked")
	private <T> void removeListener(Pair<NotificationType<?>, NotificationListener<?>> pair) throws RemoteException {
		clientSession.getRemoteConnection()
				.removeNotificationListener(remoteId,
				(NotificationType<T>) pair.getLeft(),
				(NotificationListener<T>) pair.getRight());

	}

	@Override
	public String toString() {
		return "Client: " + remoteId;
	}
	
}
