package org.oddjob.web.gson;

import com.google.gson.Gson;
import org.oddjob.remote.*;
import org.oddjob.web.JsonRemoteConnection;

/**
 * Provide a {@link RemoteConnection} by calling a {@link JsonRemoteConnection}
 * using GSON.
 */
public class RemoteConnectionGson implements RemoteConnection {

    private final RemoteInvoker remoteInvoker;

    public RemoteConnectionGson(RemoteInvoker remoteInvoker) {
        this.remoteInvoker = remoteInvoker;
    }

    public static RemoteConnection to(JsonRemoteConnection jsonRemoteInvoker, Gson gson) {

        RemoteInvoker remoteInvoker = RemoteInvokerGson.to(jsonRemoteInvoker, gson);

        return new RemoteConnectionGson(remoteInvoker);
    }

    @Override
    public <T> T invoke(long remoteId, OperationType<T> operationType, Object... args) throws RemoteException {
        return remoteInvoker.invoke(remoteId, operationType, args);
    }

    @Override
    public <T> void addNotificationListener(long remoteId, NotificationType<T> notificationType, NotificationListener<T> notificationListener) throws RemoteException {
        throw new UnsupportedOperationException("TBD");
    }

    @Override
    public <T> void removeNotificationListener(long remoteId, NotificationType<T> notificationType, NotificationListener<T> notificationListener) throws RemoteException {
        throw new UnsupportedOperationException("TBD");
    }

    @Override
    public void destroy(long remoteId) throws RemoteException {

    }

    @Override
    public void close() throws RemoteException {

    }

}
