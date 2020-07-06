package org.oddjob.websocket;

import org.oddjob.remote.*;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages {@link NotificationListener}s.
 * <p/>
 * Threading guarantees aren't perfect. This needs fixing.
 */
public class NotificationManager implements RemoteNotifier {

    @FunctionalInterface
    public interface Action {
        void perform(long remoteId, NotificationType<?> type) throws RemoteException;
    }

    private final Action subscribeAction;

    private final Action unSubscribeAction;

    private final ConcurrentMap<Long, ByTypeListeners> byRemote =
            new ConcurrentHashMap<>();

    public NotificationManager(Action subscribeAction, Action unSubscribeAction) {
        this.subscribeAction = subscribeAction;
        this.unSubscribeAction = unSubscribeAction;
    }

    private final NotificationListener<?> notificationListener =
            (NotificationListener<Object>) NotificationManager.this::handleNotification;

    public <T> NotificationListener<T> getNotificationListener() {
        return (NotificationListener<T>) notificationListener;
    }

    @Override
    public <T> void addNotificationListener(long remoteId,
                                        NotificationType<T> notificationType,
                                        NotificationListener<T> notificationListener)
            throws RemoteException {

        byRemote.computeIfAbsent(remoteId, k -> new ByTypeListeners())
                .addNotificationListener(notificationType, notificationListener,
                        type -> {
                            if (subscribeAction != null) {
                                subscribeAction.perform(remoteId, type);
                            }
                        });
    }

    @Override
    public <T> void removeNotificationListener(long remoteId,
                                           NotificationType<T> notificationType,
                                           NotificationListener<T> notificationListener) throws RemoteException {

        ByTypeListeners btl = byRemote.get(remoteId);
        if (btl == null) {
            return;
        }

        btl.removeNotificationListener(notificationType, notificationListener,
                (type) ->
                {
                    byRemote.remove(remoteId);
                    if (unSubscribeAction != null) {
                        unSubscribeAction.perform(remoteId, type);
                    }
                });
    }

    public void handleNotification(Notification<?> notification) {

        Optional.ofNullable(byRemote.get(notification.getRemoteId()))
                .ifPresent(btl -> btl.dispatch(notification));
    }

    @FunctionalInterface
    private interface WithType<T> {
        void apply(NotificationType<T> type) throws RemoteException;
    }

    static class ByTypeListeners {

        private final ConcurrentMap<NotificationType<?>, Set<NotificationListener<?>>> byType =
                new ConcurrentHashMap<>();

        <T> void addNotificationListener(NotificationType<T> notificationType,
                                     NotificationListener<T> notificationListener,
                                     WithType<T> whenNew) throws RemoteException {

            AtomicBoolean subscribe = new AtomicBoolean();

            byType.computeIfAbsent(notificationType, k -> {
                subscribe.set(true);
                return ConcurrentHashMap.newKeySet();
            }).add(notificationListener);

            if (subscribe.get()) {
                whenNew.apply(notificationType);
            }
        }

        <T> void removeNotificationListener(NotificationType<T> notificationType,
                                        NotificationListener<T> notificationListener,
                                        WithType<T> onEmpty) throws RemoteException {
            Set<NotificationListener<?>> listeners = byType.get(notificationType);
            if (listeners == null) {
                return;
            }

            listeners.remove(notificationListener);

            if (listeners.isEmpty()) {
                byType.remove(notificationType);
                onEmpty.apply(notificationType);
            }
        }

        @SuppressWarnings("unchecked")
        <T> void dispatch(Notification<T> notification) {

            Optional.ofNullable(byType.get(notification.getType()))
                    .ifPresent(nls -> nls.forEach(
                            nl -> ((NotificationListener<T>) nl).handleNotification(notification)));
        }
    }

}
