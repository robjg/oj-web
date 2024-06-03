package org.oddjob.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.http.HttpStatus;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteComponentException;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Http Client for a Remote Invoker.
 */
public class InvokerClient implements RemoteInvoker, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(InvokerClient.class);

    private final URI uri;

    private final Gson gson;

    private final HttpClient httpClient;

    private InvokerClient(URI uri, Gson gson) throws RemoteException {
        this.uri = uri;
        this.gson = gson;
        this.httpClient = new HttpClient();

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }

    public static InvokerClient create(URI uri, Gson gson) throws RemoteException {
        return new InvokerClient(uri, gson);
    }


    @Override
    public <T> T invoke(long remoteId, OperationType<T> operationType, Object... args) throws RemoteException {


        InvokeRequest invokeRequest = new InvokeRequest(remoteId,
                operationType,
                args);

        String jsonRequest = gson.toJson(invokeRequest);

        logger.debug("Request: {}", jsonRequest);

        Request request = httpClient.POST(uri);

        request.body(new StringRequestContent("application/json", jsonRequest));

        ContentResponse response;
        try {
            response =  request.send();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (TimeoutException | ExecutionException e) {
            throw new RemoteComponentException(remoteId, e);
        }

        int status = response.getStatus();
        String content = response.getContentAsString();

        if (HttpStatus.OK_200  == status) {
            logger.debug("Response OK: " + content);
        }
        else {
            String message = "Response " + HttpStatus.getCode(status);
            logger.info(message);
            throw new RemoteComponentException(remoteId, "Request failed: " + message);
        }

        Type collectionType = TypeToken.getParameterized(InvokeResponse.class,
                operationType.getReturnType()).getType();

        InvokeResponse<T> invokeResponse = gson.fromJson(content, collectionType);

        return invokeResponse.getValue();
    }

    @Override
    public void close() throws RemoteException {

        try {
            httpClient.stop();
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }
}
