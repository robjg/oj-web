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
public class NotificationManager implements RemoteNotifier, NotificationListener {

    @FunctionalInterface
    public interface Action {
        void perform(long remoteId, String type) throws RemoteException;
    }

    private final NotificationInfoProvider notificationInfoProvider;

    private final Action subscribeAction;

    private final Action unSubscribeAction;

    private final ConcurrentMap<Long, ByTypeListeners> byRemote =
            new ConcurrentHashMap<>();

    public NotificationManager(NotificationInfoProvider notificationInfoProvider, Action subscribeAction, Action unSubscribeAction) {
        this.notificationInfoProvider = notificationInfoProvider;
        this.subscribeAction = subscribeAction;
        this.unSubscribeAction = unSubscribeAction;
    }

    @Override
    public NotificationInfo getNotificationInfo(long remoteId) throws RemoteException {
        return notificationInfoProvider.getNotificationInfo(remoteId);
    }

    @Override
    public void addNotificationListener(long remoteId,
                                        String notificationType,
                                        NotificationListener notificationListener)
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
    public void removeNotificationListener(long remoteId,
                                           String notificationType,
                                           NotificationListener notificationListener) throws RemoteException {

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

    @Override
    public void handleNotification(Notification notification) {

        Optional.ofNullable(byRemote.get(notification.getRemoteId()))
                .ifPresent(btl -> btl.dispatch(notification));
    }

    @FunctionalInterface
    private interface WithType {
        void apply(String type) throws RemoteException;
    }

    static class ByTypeListeners {

        private final ConcurrentMap<String, Set<NotificationListener>> byType =
                new ConcurrentHashMap<>();

        void addNotificationListener(String notificationType,
                                     NotificationListener notificationListener,
                                     WithType whenNew) throws RemoteException {

            AtomicBoolean subscribe = new AtomicBoolean();

            byType.computeIfAbsent(notificationType, k -> {
                subscribe.set(true);
                return ConcurrentHashMap.newKeySet();
            }).add(notificationListener);

            if (subscribe.get()) {
                whenNew.apply(notificationType);
            }
        }

        void removeNotificationListener(String notificationType,
                                        NotificationListener notificationListener,
                                        WithType onEmpty) throws RemoteException {
            Set<NotificationListener> listeners = byType.get(notificationType);
            if (listeners == null) {
                return;
            }

            listeners.remove(notificationListener);

            if (listeners.isEmpty()) {
                byType.remove(notificationType);
                onEmpty.apply(notificationType);
            }
        }

        void dispatch(Notification notification) {

            Optional.ofNullable(byType.get(notification.getType()))
                    .ifPresent(nls -> nls.forEach(
                            nl -> nl.handleNotification(notification)));
        }
    }

}
