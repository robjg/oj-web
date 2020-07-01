package org.oddjob.websocket;

import org.oddjob.remote.RemoteNotifier;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Creates an {@link NotifierServerEndpoint} with an {@link RemoteNotifier}.
 */
public class NotifierConfigurator extends ServerEndpointConfig.Configurator {

    private final RemoteNotifier remoteNotifier;

    public NotifierConfigurator(RemoteNotifier remoteNotifier) {
        this.remoteNotifier = remoteNotifier;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        if (remoteNotifier == null) {
            throw new InstantiationException("No Remote Notifier");
        }
        return endpointClass.cast(new NotifierServerEndpoint(remoteNotifier));
    }
}
