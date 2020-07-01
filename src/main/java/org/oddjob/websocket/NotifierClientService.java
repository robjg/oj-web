package org.oddjob.websocket;

import org.oddjob.remote.*;

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

    private NotificationInfoProvider notificationInfoProvider;

    private NotifierClientEndpoint endpoint;

    public void start() throws IOException, DeploymentException {

        Objects.requireNonNull(uri);

        javax.websocket.WebSocketContainer container =
                javax.websocket.ContainerProvider.getWebSocketContainer();

        this.endpoint = new NotifierClientEndpoint(notificationInfoProvider);

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
    public NotificationInfo getNotificationInfo(long remoteId) throws RemoteException {
        return notificationInfoProvider.getNotificationInfo(remoteId);
    }

    @Override
    public void addNotificationListener(long remoteId, String notificationType, NotificationListener notificationListener) throws RemoteException {
        NotifierClientEndpoint endpoint = this.endpoint;
        if (endpoint != null) {
            endpoint.addNotificationListener(remoteId, notificationType, notificationListener);
        }
    }

    @Override
    public void removeNotificationListener(long remoteId, String notificationType, NotificationListener notificationListener) throws RemoteException {
        NotifierClientEndpoint endpoint = this.endpoint;
        if (endpoint != null) {
            endpoint.removeNotificationListener(remoteId, notificationType, notificationListener);
        }
    }

    public NotificationInfoProvider getNotificationInfoProvider() {
        return notificationInfoProvider;
    }

    public void setNotificationInfoProvider(NotificationInfoProvider notificationInfoProvider) {
        this.notificationInfoProvider = notificationInfoProvider;
    }
}
