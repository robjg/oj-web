package org.oddjob.web.gson;

import com.google.gson.Gson;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.http.InvokeRequest;
import org.oddjob.http.InvokeResponse;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteInvoker;
import org.oddjob.web.JsonRemoteInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide a {@link JsonRemoteInvoker} by calling a {@link RemoteInvoker}
 * using GSON.
 */
public class GsonRemoteInvoker implements JsonRemoteInvoker {

    private static final Logger logger = LoggerFactory.getLogger(GsonRemoteInvoker.class);

    private final RemoteInvoker remoteInvoker;

    private final Gson gson;

    public GsonRemoteInvoker(RemoteInvoker remoteInvoker, Gson gson) {
        this.remoteInvoker = remoteInvoker;
        this.gson = gson;
    }

    public static JsonRemoteInvoker to(RemoteInvoker remoteInvoker) {
        return to(remoteInvoker, GsonUtil.createGson(GsonRemoteInvoker.class.getClassLoader()));
    }

    public static JsonRemoteInvoker to(RemoteInvoker remoteInvoker, Gson gson) {
        return new GsonRemoteInvoker(remoteInvoker, gson);
    }

    @Override
    public String invoke(String jsonRequest)  throws RemoteException {

        InvokeRequest invokeRequest;
        try {
            invokeRequest = gson.fromJson(jsonRequest, InvokeRequest.class);
        }
        catch (RuntimeException e) {
            throw new RemoteException("Failed parsing: " + jsonRequest, e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Request:" + invokeRequest);
        }

        OperationType<?> operationType = invokeRequest.getOperationType();

        Object result = this.remoteInvoker.invoke(invokeRequest.getRemoteId(),
                    operationType,
                    invokeRequest.getArgs());

        Class<?> returnType = operationType.getReturnType();
        if (result != null && result.getClass() != returnType) {
            returnType = result.getClass();
            if (logger.isDebugEnabled()) {
                logger.debug("Actual return type is " + returnType.getName());
            }
        }

        InvokeResponse<?> invokeResponse = inferResponseType(returnType, result);

        String response = gson.toJson(invokeResponse);

        logger.debug("Response:" + gson.toJson(invokeResponse));

        return response;
    }

    <T> InvokeResponse<T> inferResponseType(Class<T> type, Object value) {
        return InvokeResponse.from(type, ClassUtils.cast(type, value));
    }
}
