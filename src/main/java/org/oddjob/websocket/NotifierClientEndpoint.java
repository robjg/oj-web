package org.oddjob.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oddjob.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Web Socket client endpoint for a Remote Notifier.
 *
 */
@ClientEndpoint
public class NotifierClientEndpoint implements RemoteNotifier {

    private static final Logger logger = LoggerFactory.getLogger(NotifierClientEndpoint.class);

    public static final long TIMEOUT_SECONDS = 5L;

    private final Map<String, Set<NotificationListener>> listeners
            = new HashMap<>();

    private final NotificationManager notificationManager;

    private final Gson gson;

    private Session session;

    public NotifierClientEndpoint(NotificationInfoProvider notificationInfoProvider) {
        Objects.requireNonNull(notificationInfoProvider);

        this.notificationManager = new NotificationManager(notificationInfoProvider,
                this::subscribe,
                this::unsubscribe);

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Notification.class,
                        new NotificationDeserializer(notificationInfoProvider))
                .create();
    }

    @OnOpen
    public void open(Session session, EndpointConfig config) {

        if (logger.isDebugEnabled()) {
            logger.debug("Opening session " + session.getId() + ", user properties: " + config.getUserProperties());
        }

        this.session = session;
    }

    @OnMessage
    public void onMessage(Session session, String message) {

        if (logger.isDebugEnabled()) {
            logger.debug("Message from {}: {}", session.getId(), message);
        }

        Notification notification = gson.fromJson(message, Notification.class);

        notificationManager.handleNotification(notification);
    }

    @OnClose
    public void close(Session session) {

    }

    @OnError
    public void onError(Session session, Throwable t) {
    }

    @Override
    public NotificationInfo getNotificationInfo(long remoteId) throws RemoteException {
        return notificationManager.getNotificationInfo(remoteId);
    }

    @Override
    public void addNotificationListener(long remoteId, String notificationType, NotificationListener notificationListener) throws RemoteException {

        notificationManager.addNotificationListener(remoteId, notificationType, notificationListener);
    }

    @Override
    public void removeNotificationListener(long remoteId, String notificationType, NotificationListener notificationListener) throws RemoteException {

        notificationManager.removeNotificationListener(remoteId, notificationType, notificationListener);
    }

    void subscribe(long remoteId, String type) throws RemoteException {

        if (remoteId == NotifierServerEndpoint.SYSTEM_REMOTE_ID) {
            return;
        }

        logger.debug("Subscribing to remote Id {}, {}", remoteId, type);

        SubscriptionRequest request = new SubscriptionRequest();
        request.setAction(SubscriptionRequest.Action.ADD);
        request.setRemoteId(remoteId);
        request.setType(type);

        CountDownLatch latch = new CountDownLatch(1);

        NotificationListener listener = notification -> {
            if (notification.getData().equals(request)) {
                latch.countDown();
            }
        };

        this.addNotificationListener(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                listener);

        try {
            session.getBasicRemote().sendText(gson.toJson(request));
        } catch (IOException e) {
            throw new RemoteIdException(remoteId, e);
        }

        try {
            if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new RemoteIdException(remoteId, "Time out waiting for " + request);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.removeNotificationListener(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                listener);
    }

    void unsubscribe(long remoteId, String type) throws RemoteException {

        if (remoteId == NotifierServerEndpoint.SYSTEM_REMOTE_ID) {
            return;
        }

        logger.debug("Unsubscribing from remote Id {}, {}", remoteId, type);

        SubscriptionRequest request = new SubscriptionRequest();
        request.setAction(SubscriptionRequest.Action.REMOVE);
        request.setRemoteId(remoteId);
        request.setType(type);

        CountDownLatch latch = new CountDownLatch(1);

        NotificationListener listener = notification -> {
            if (notification.getData().equals(request)) {
                latch.countDown();
            }
        };

        this.addNotificationListener(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                listener);

        try {
            session.getBasicRemote().sendText(gson.toJson(request));
        } catch (IOException e) {
            throw new RemoteIdException(remoteId, e);
        }

        try {
            if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new RemoteIdException(remoteId, "Time out waiting for " + request);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.removeNotificationListener(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                listener);
    }

}