package org.oddjob.web.client;

import com.google.gson.Gson;
import org.oddjob.http.InvokerClient;
import org.oddjob.remote.*;
import org.oddjob.websocket.NotifierClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Client Connect that connects to a {@link org.oddjob.web.WebServerHandler}.
 */
public class WebRemoteConnector {

    public static RemoteConnection connect(String host, int port,
                                           ScheduledExecutorService executor,
                                           Gson gson) throws RemoteException {

        Objects.requireNonNull(host);

        String connection = host + ":" + (port == 0 ? 80 : port);

        URI notifierUri;
        URI invokerUri;

        try {
            notifierUri = new URI("ws://" + connection  + "/notifier");
            invokerUri = new URI("http://" + connection + "/invoke");
        }
        catch (URISyntaxException e) {
            throw new RemoteException(e);
        }

        NotifierClient notifierClient = NotifierClient.create(notifierUri, executor, gson);
        InvokerClient invokerClient = InvokerClient.create(invokerUri, gson);

        return new WebConnection(notifierClient, invokerClient);

    }

    static class WebConnection implements RemoteConnection {

        private final NotifierClient notifierClient;
        private final InvokerClient invokerClient;

        WebConnection(NotifierClient notifierClient, InvokerClient invokerClient) {
            this.notifierClient = notifierClient;
            this.invokerClient = invokerClient;
        }

        public void close() throws RemoteException {

            try {
                notifierClient.close();
            }
            finally {
                invokerClient.close();
            }
        }

        @Override
        public <T> T invoke(long remoteId, OperationType<T> operationType, Object... args) throws RemoteException {
            return invokerClient.invoke(remoteId, operationType, args);
        }

        @Override
        public <T> void addNotificationListener(long remoteId, NotificationType<T> notificationType, NotificationListener<T> notificationListener) throws RemoteException {
            notifierClient.addNotificationListener(remoteId, notificationType, notificationListener);
        }

        @Override
        public <T> void removeNotificationListener(long remoteId, NotificationType<T> notificationType, NotificationListener<T> notificationListener) throws RemoteException {
            notifierClient.removeNotificationListener(remoteId, notificationType, notificationListener);
        }

        @Override
        public void destroy(long remoteId) throws RemoteException {
            // TODO - check all listeners removed for the given remoteId.
        }
    }

}
