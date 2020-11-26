package org.oddjob.websocket;

import com.google.gson.Gson;
import org.oddjob.remote.*;
import org.oddjob.web.gson.GsonUtil;
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

    public static final NotificationType<SubscriptionRequest> ACTION_COMPLETE_TYPE =
            NotificationType.ofName("notifier.action.complete")
            .andDataType(SubscriptionRequest.class);

    public static final long SYSTEM_REMOTE_ID = -1L;

    public static NotificationType<Void> HEARTBEAT_TYPE =
            NotificationType.ofName("heartbeat")
                    .andNoData();

    public static final SubscriptionRequest HEARTBEAT_REQUEST = new SubscriptionRequest(
            SubscriptionRequest.Action.HEARTBEAT,
            SYSTEM_REMOTE_ID,
            HEARTBEAT_TYPE);

    private final NotificationManager notificationManager;

    private final AtomicLong systemSequence = new AtomicLong();

    private final Gson gson;

    public NotifierServerEndpoint(RemoteNotifier remoteNotifier) {

        AtomicReference<NotificationManager> manager = new AtomicReference<>();

        this.notificationManager = new NotificationManager(
                (remoteId, type) -> remoteNotifier.addNotificationListener(remoteId, type,
                        manager.get().getNotificationListener()),
                (remoteId, type) -> remoteNotifier.removeNotificationListener(remoteId, type,
                        manager.get().getNotificationListener()));

        manager.set(notificationManager);

        this.gson = GsonUtil.createGson(getClass().getClassLoader());
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

        SubscriptionRequest request = gson.fromJson(message,
                SubscriptionRequest.class);

        switch (request.getAction()) {
            case ADD:
                notificationManager.addNotificationListener(
                        request.getRemoteId(), request.getType(),
                        new SessionListener<>(session));
                break;
            case REMOVE:
                notificationManager.removeNotificationListener(
                        request.getRemoteId(), request.getType(),
                        new SessionListener<>(session));
                break;
            case HEARTBEAT:
                break;
            default:
                throw new IllegalStateException();
        }

        Notification<SubscriptionRequest> response = new Notification<>(SYSTEM_REMOTE_ID, ACTION_COMPLETE_TYPE,
                systemSequence.getAndIncrement(), request);

        return gson.toJson(response);
    }

    @OnClose
    public void close(Session session) {
        logger.debug("Closed session " + session.getId());
    }

    @OnError
    public void error(Session session, Throwable t) {
        logger.error("Websocket error for session id " + session.getId(),
                t);
    }

    class SessionListener<T> implements NotificationListener<T> {

        private final Session session;

        SessionListener(Session session) {
            this.session = session;
        }

        @Override
        public void handleNotification(Notification<T> notification) {

            try {
                String json = gson.toJson(notification);
                if (logger.isDebugEnabled()) {
                    logger.debug("Sending: " + json);
                }
                session.getBasicRemote().sendText(json);
            } catch (IOException e) {
                logger.error("Failed sending notification" + notification, e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SessionListener<?> that = (SessionListener<?>) o;
            return Objects.equals(session.getId(), that.session.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(session.getId());
        }
    }

}
