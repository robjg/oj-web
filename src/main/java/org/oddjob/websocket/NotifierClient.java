package org.oddjob.websocket;

import org.oddjob.remote.NotificationListener;
import org.oddjob.remote.NotificationType;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteNotifier;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * An Oddjob style service that provides a Web Socket {@link RemoteNotifier}.
 */
public class NotifierClient implements RemoteNotifier, AutoCloseable {

    public final Session session;

    private final NotifierClientEndpoint endpoint;

    private NotifierClient(Session session, NotifierClientEndpoint endpoint) {
        this.session = session;
        this.endpoint = endpoint;
    }

    public static NotifierClient create(URI uri, Executor executor) throws RemoteException {

        Objects.requireNonNull(uri);

        javax.websocket.WebSocketContainer container =
                javax.websocket.ContainerProvider.getWebSocketContainer();

        NotifierClientEndpoint endpoint = new NotifierClientEndpoint(executor);

        try {
            return new NotifierClient(container.connectToServer(endpoint, uri), endpoint);
        } catch (DeploymentException | IOException e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public void close() throws RemoteException {

        try {
            this.session.close();
        } catch (IOException e) {
            throw new RemoteException(e);
        }
    }

    public String getSessionId() {
        return Optional.ofNullable(this.session)
                .map(Session::getId)
                .orElse(null);
    }

    @Override
    public <T> void addNotificationListener(long remoteId,
                                            NotificationType<T> notificationType,
                                            NotificationListener<T> notificationListener) throws RemoteException {
        NotifierClientEndpoint endpoint = this.endpoint;
        if (endpoint != null) {
            endpoint.addNotificationListener(remoteId, notificationType, notificationListener);
        }
    }

    @Override
    public <T> void removeNotificationListener(long remoteId,
                                               NotificationType<T> notificationType,
                                               NotificationListener<T> notificationListener) throws RemoteException {
        NotifierClientEndpoint endpoint = this.endpoint;
        if (endpoint != null) {
            endpoint.removeNotificationListener(remoteId, notificationType, notificationListener);
        }
    }
}
