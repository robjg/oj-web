package org.oddjob.websocket;

import com.google.gson.Gson;
import org.oddjob.remote.*;
import org.oddjob.remote.util.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Web Socket client endpoint for a Remote Notifier.
 *
 */
@ClientEndpoint
public class NotifierClientEndpoint implements RemoteNotifier {

    private static final Logger logger = LoggerFactory.getLogger(NotifierClientEndpoint.class);

    public static final long HEARTBEAT_SECONDS = 10L;

    public static final long TIMEOUT_SECONDS = 5L;

    private final NotificationManager notificationManager;

    private final ScheduledExecutorService executor;

    private final Gson gson;

    private final Clock clock;

    private Session session;

    private long lastMessage;

    private ScheduledFuture<?> heartbeatFuture;

    private Instant lastMessageTime;

    public NotifierClientEndpoint(ScheduledExecutorService executor, Gson gson) {
        this(executor, Clock.systemDefaultZone(), gson);
    }

    public NotifierClientEndpoint(ScheduledExecutorService executor,
                                  Clock clock,
                                  Gson gson) {
        this.executor = executor;
        this.clock = clock;

        this.notificationManager = new NotificationManager(
                this::subscribe,
                this::unsubscribe);

        this.gson = gson;

        this.lastMessageTime = Instant.EPOCH;
    }

    @OnOpen
    public void open(Session session, EndpointConfig config) {

        if (logger.isDebugEnabled()) {
            logger.debug("Opening session " + session.getId() + ", user properties: " + config.getUserProperties());
        }

        this.session = session;

        this.heartbeatFuture = executor.scheduleWithFixedDelay(() -> {

            Instant now = clock.instant();
            if (Duration.between(this.lastMessageTime,
                    clock.instant()).getSeconds() >= HEARTBEAT_SECONDS) {
                try {
                    session.getBasicRemote()
                            .sendText(gson.toJson(NotifierServerEndpoint.HEARTBEAT_REQUEST));
                } catch (IOException e) {
                    logger.error("Failed sending heartbeat", e);
                }
            }
        }, HEARTBEAT_SECONDS, HEARTBEAT_SECONDS, TimeUnit.SECONDS);
    }

    @OnMessage
    public void onMessage(Session session, String message) {

        if (logger.isDebugEnabled()) {
            logger.debug("Message from {}: {}", session.getId(), message);
        }

        this.lastMessageTime = clock.instant();

        Notification<?> notification = gson.fromJson(message, Notification.class);

        // Process System notification on this thread otherwise unsubscribe events can deadlock
        if (notification.getRemoteId() == NotifierServerEndpoint.SYSTEM_REMOTE_ID) {
            notificationManager.handleNotification(notification);
        }
        else {
            executor.execute(() -> notificationManager.handleNotification(notification));
        }
    }

    @OnClose
    public void close(Session session) {
        this.heartbeatFuture.cancel(false);
        logger.debug("Closed session " + session.getId());
    }

    @OnError
    public void error(Session session, Throwable t) {
        logger.error("Websocket error for session id " + session.getId(),
                t);
    }

    @Override
    public <T> void addNotificationListener(long remoteId,
                                            NotificationType<T> notificationType,
                                            NotificationListener<T> notificationListener) throws RemoteException {

        notificationManager.addNotificationListener(remoteId, notificationType, notificationListener);
    }

    @Override
    public <T> void removeNotificationListener(long remoteId,
                                               NotificationType<T> notificationType,
                                               NotificationListener<T> notificationListener) throws RemoteException {

        notificationManager.removeNotificationListener(remoteId, notificationType, notificationListener);
    }

    <T> void subscribe(long remoteId, NotificationType<T> type) throws RemoteException {

        if (remoteId == NotifierServerEndpoint.SYSTEM_REMOTE_ID) {
            return;
        }

        logger.debug("Subscribing to remote Id {}, {}", remoteId, type);

        SubscriptionRequest request = new SubscriptionRequest(
                SubscriptionRequest.Action.ADD, remoteId, type);

        CountDownLatch latch = new CountDownLatch(1);

        NotificationListener<SubscriptionRequest> listener = notification -> {
            logger.debug("Subscribe callback received: " + notification);
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
            throw new RemoteComponentException(remoteId, e);
        }

        try {
            if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new RemoteComponentException(remoteId, "Time out waiting for " + request);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.removeNotificationListener(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                listener);
    }

    <T> void unsubscribe(long remoteId, NotificationType<T> type) throws RemoteException {

        if (remoteId == NotifierServerEndpoint.SYSTEM_REMOTE_ID) {
            return;
        }

        logger.debug("Unsubscribing from remote Id {}, {}", remoteId, type);

        SubscriptionRequest request = new SubscriptionRequest(
                SubscriptionRequest.Action.REMOVE, remoteId, type);

        CountDownLatch latch = new CountDownLatch(1);

        NotificationListener<SubscriptionRequest> listener = notification -> {
            logger.debug("Unsubscribe callback received: " + notification);
            if (notification.getData().equals(request)) {
                latch.countDown();
            }
        };

        this.addNotificationListener(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                listener);

        try {
            session.getBasicRemote().sendText(gson.toJson(request));

            try {
                if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    throw new RemoteComponentException(remoteId, "Time out waiting for " + request);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        catch (IOException e) {
            // We just keep going here
            logger.warn("Failed sending unsubscribe request for " + remoteId + " of type " + type, e);
        }

        this.removeNotificationListener(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                listener);
    }

}