package org.oddjob.websocket;

import com.google.gson.Gson;
import org.oddjob.remote.RemoteNotifier;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Creates an {@link NotifierServerEndpoint} with an {@link RemoteNotifier}.
 */
public class NotifierConfigurator extends ServerEndpointConfig.Configurator {

    private final RemoteNotifier remoteNotifier;

    private final Gson gson;

    public NotifierConfigurator(RemoteNotifier remoteNotifier,
                                Gson gson) {
        this.remoteNotifier = remoteNotifier;
        this.gson = gson;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        if (remoteNotifier == null) {
            throw new InstantiationException("No Remote Notifier");
        }
        return endpointClass.cast(new NotifierServerEndpoint(remoteNotifier, gson));
    }
}
