package org.oddjob.web.gson;

import com.google.gson.Gson;
import org.oddjob.remote.RemoteConnection;
import org.oddjob.remote.RemoteException;
import org.oddjob.web.JsonRemoteConnection;
import org.oddjob.web.JsonRemoteInvoker;

/**
 * Provide a {@link JsonRemoteConnection} by calling a {@link RemoteConnection}
 * using GSON.
 */
public class GsonRemoteConnection implements JsonRemoteConnection {

    private final JsonRemoteInvoker jsonRemoteInvoker;

    public GsonRemoteConnection(JsonRemoteInvoker jsonRemoteInvoker) {
        this.jsonRemoteInvoker = jsonRemoteInvoker;
    }

    public static JsonRemoteConnection to(RemoteConnection remoteConnection) {

        Gson gson = GsonUtil.createGson(RemoteConnectionGson.class.getClassLoader());

        JsonRemoteInvoker jsonRemoteInvoker = GsonRemoteInvoker.to(remoteConnection, gson);

        return new GsonRemoteConnection(jsonRemoteInvoker);
    }

    @Override
    public String invoke(String jsonRequest) throws RemoteException {
        return jsonRemoteInvoker.invoke(jsonRequest);
    }
}
