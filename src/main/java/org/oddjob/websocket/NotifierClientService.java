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

/**
 * An Oddjob style service that provides a Web Socket {@link RemoteNotifier}.
 */
public class NotifierClientService implements RemoteNotifier {

    private URI uri;

    public Session session;

    private NotifierClientEndpoint endpoint;

    public void start() throws IOException, DeploymentException {

        Objects.requireNonNull(uri);

        javax.websocket.WebSocketContainer container =
                javax.websocket.ContainerProvider.getWebSocketContainer();

        this.endpoint = new NotifierClientEndpoint();

        this.session = container.connectToServer(endpoint, uri);
    }

    public void stop() throws IOException {

        this.session.close();

        this.session = null;
        this.endpoint = null;
    }

    public String getSessionId() {
        return Optional.ofNullable(this.session)
                .map(Session::getId)
                .orElse(null);
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
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
