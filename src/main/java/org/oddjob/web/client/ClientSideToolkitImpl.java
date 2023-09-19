package org.oddjob.web.client;

import org.oddjob.arooa.utils.Pair;
import org.oddjob.jmx.RemoteOperation;
import org.oddjob.jmx.Utils;
import org.oddjob.jmx.client.ClientSession;
import org.oddjob.jmx.client.ClientSideToolkit;
import org.oddjob.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.NotSerializableException;
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

	private enum Phase {
		ACTIVE,
		DESTROYED,
	}

	private volatile Phase phase = Phase.ACTIVE;

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
	public <T> T invoke(RemoteOperation<T> remote, Object... args) throws RemoteException {
		if (phase == Phase.DESTROYED) {
			throw new IllegalStateException("Node destroyed: " + remoteId);
		}

		OperationType<T> operationType;
		if (remote instanceof HasOperationType) {
			operationType = ((HasOperationType<T>) remote).getOperationType();
		}
		else {
			throw new IllegalArgumentException("No operation type" + remote);
		}

		Objects.requireNonNull(remote);

		Object[] exported;
		try {
			exported = Utils.export(args);
		} catch (NotSerializableException e) {
			throw new RemoteException("Failed exporting args for " + operationType, e);
		}

		Object result = clientSession.getRemoteConnection().invoke(
					remoteId,
					operationType,
					exported);

		return (T) Utils.importResolve(result, clientSession);

	}

	@Override
	public <T> void registerNotificationListener(NotificationType<T> eventType,
												 NotificationListener<T> notificationListener)
	throws RemoteException {
		if (phase == Phase.DESTROYED) {
			throw new IllegalStateException("Node destroyed: " + remoteId);
		}

		clientSession.getRemoteConnection()
				.addNotificationListener(remoteId, eventType, notificationListener);
		listeners.add(Pair.of(eventType, notificationListener));
	}

	@Override
	public <T> void removeNotificationListener(NotificationType<T> eventType,
			NotificationListener<T> notificationListener)
	throws RemoteException {
		if (phase == Phase.DESTROYED) {
			throw new IllegalStateException("Node destroyed: " + remoteId);
		}

		clientSession.getRemoteConnection()
				.removeNotificationListener(remoteId, eventType, notificationListener);
		listeners.remove(Pair.of(eventType, notificationListener));
	}
	
	public ClientSession getClientSession() {
		return clientSession;
	}
	
	/**
	 * Destroy this node. Clean up resources, remove remote connections.
	 */
	void destroy() {
		phase = Phase.DESTROYED;
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
		logger.debug("Destroyed client for [" + this + "]");
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
