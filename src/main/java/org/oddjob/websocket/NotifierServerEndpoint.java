package org.oddjob.websocket;

import com.google.gson.Gson;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationListener;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Web Socket server endpoint for a Remote Notifier.
 *
 */
@ServerEndpoint("/notifier")
public class NotifierServerEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(NotifierServerEndpoint.class);

    public static final String ACTION_COMPLETE_TYPE = "notifier.action.complete";

    public static final long SYSTEM_REMOTE_ID = -1L;

    private final NotificationManager notificationManager;

    private final AtomicLong systemSequence = new AtomicLong();

    private final Gson gson = new Gson();

    public NotifierServerEndpoint(RemoteNotifier remoteNotifier) {

        AtomicReference<NotificationListener> listener = new AtomicReference<>();

        this.notificationManager = new NotificationManager(
                remoteNotifier,
                (remoteId, type) -> remoteNotifier.addNotificationListener(remoteId, type, listener.get()),
                (remoteId, type) -> remoteNotifier.removeNotificationListener(remoteId, type, listener.get()));

        listener.set(notificationManager);
    }


    @OnOpen
    public void open(Session session, EndpointConfig config) {

        if (logger.isDebugEnabled()) {
            logger.debug("Opening session " + session.getId() + ", user properties: " + config.getUserProperties());
        }
    }

    @OnMessage
    public String onMessage(Session session, String message) throws RemoteException {

        if (logger.isDebugEnabled()) {
            logger.debug("Message from {}: {}", session.getId(), message);
        }

        SubscriptionRequest request = gson.fromJson(message, SubscriptionRequest.class);

        switch (request.getAction()) {
            case ADD:
                notificationManager.addNotificationListener(
                        request.getRemoteId(), request.getType(),
                        new SessionListener(session));
                break;
            case REMOVE:
                notificationManager.removeNotificationListener(
                        request.getRemoteId(), request.getType(),
                        new SessionListener(session));
                break;
            default:
                throw new IllegalStateException();
        }

        Notification response = new Notification(SYSTEM_REMOTE_ID, ACTION_COMPLETE_TYPE,
                systemSequence.getAndIncrement(), request);

        return gson.toJson(response);
    }

    @OnClose
    public void close(Session session) {

    }

    @OnError
    public void error(Session session, Throwable t) {

    }

    static class SessionListener implements NotificationListener {

        private final Session session;

        SessionListener(Session session) {
            this.session = session;
        }

        @Override
        public void handleNotification(Notification notification) {

            try {
                session.getBasicRemote().sendText(new Gson().toJson(notification));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SessionListener that = (SessionListener) o;
            return Objects.equals(session.getId(), that.session.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(session.getId());
        }
    }

}
