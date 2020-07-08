package org.oddjob.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpStatus;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteIdException;
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

    private InvokerClient(URI uri) throws RemoteException {
        this.uri = uri;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(OperationType.class,
                        new OperationTypeDeSer(getClass().getClassLoader()))
                .registerTypeAdapter(InvokeRequest.class,
                        new InvokeRequestDeserializer())
                .registerTypeAdapter(InvokeResponse.class,
                        new InvokeResponseDesSer(getClass().getClassLoader()))
                .create();

        this.httpClient = new HttpClient();

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }

    public static InvokerClient create(URI uri) throws RemoteException {
        return new InvokerClient(uri);
    }


    @Override
    public <T> T invoke(long remoteId, OperationType<T> operationType, Object... args) throws RemoteException {


        InvokeRequest invokeRequest = new InvokeRequest(remoteId,
                operationType,
                args);

        String jsonRequest = gson.toJson(invokeRequest);

        Request request = httpClient.POST(uri);

        request.content(new StringContentProvider(jsonRequest),
                "application/json");

        ContentResponse response;
        try {
            response =  request.send();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (TimeoutException | ExecutionException e) {
            throw new RemoteIdException(remoteId, e);
        }

        int status = response.getStatus();
        String content = response.getContentAsString();

        if (HttpStatus.OK_200  == status) {
            logger.info("Response OK, response content length " + content.length());
        }
        else {
            String message = "Response " + HttpStatus.getCode(status);
            logger.info(message);
            throw new RemoteIdException(remoteId, "Request failed: " + message);
        }

        Type collectionType = new TypeToken<InvokeResponse<T>>(){}.getType();

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
