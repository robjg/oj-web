package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.oddjob.http.InvokeRequest;
import org.oddjob.http.InvokeResponse;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteInvoker;
import org.oddjob.web.JsonRemoteInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Provide a {@link RemoteInvoker} by calling a {@link JsonRemoteInvoker}
 * using GSON.
 */
public class RemoteInvokerGson implements RemoteInvoker {

    private static final Logger logger = LoggerFactory.getLogger(RemoteInvokerGson.class);

    private final JsonRemoteInvoker jsonRemoteInvoker;

    private final Gson gson;

    public RemoteInvokerGson(JsonRemoteInvoker jsonRemoteInvoker, Gson gson) {
        this.jsonRemoteInvoker = jsonRemoteInvoker;
        this.gson = gson;
    }

    public static RemoteInvoker to(JsonRemoteInvoker jsonRemoteInvoker, Gson gson) {

        return new RemoteInvokerGson(jsonRemoteInvoker, gson);
    }

    @Override
    public <T> T invoke(long remoteId, OperationType<T> operationType, Object... args) throws RemoteException {


        InvokeRequest invokeRequest = new InvokeRequest(remoteId,
                operationType,
                args);

        String jsonRequest = gson.toJson(invokeRequest);

        logger.debug("Request: {}", jsonRequest);

        String jsonResponse = jsonRemoteInvoker.invoke(jsonRequest);

        Type collectionType = TypeToken.getParameterized(InvokeResponse.class,
                operationType.getReturnType()).getType();

        InvokeResponse<T> invokeResponse = gson.fromJson(jsonResponse, collectionType);

        return invokeResponse.getValue();
    }
}
