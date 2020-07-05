package org.oddjob.websocket;

import org.junit.Test;
import org.oddjob.jetty.JettyHttpServer;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationInfo;
import org.oddjob.remote.NotificationInfoBuilder;
import org.oddjob.remote.NotificationListener;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ClientServerTest {

    @Test
    public void testSubscribeReceiveUnsubscribe() throws Exception {

        BlockingQueue<Notification> results = new LinkedBlockingDeque<>();

        NotificationInfo info = new NotificationInfoBuilder()
                .addType(NotifierServerEndpoint.ACTION_COMPLETE_TYPE).ofClass(SubscriptionRequest.class)
                .and()
                .addType("some.string.event").ofClass(String.class)
                .build();

        NotificationManager notificationManager = new NotificationManager(
                id -> info,
                (remoteId, type) -> {
                },
                (remoteId, type) -> {
                });

        JettyHttpServer server = new JettyHttpServer();

        JettyNotifierEndpointHandler handler = new JettyNotifierEndpointHandler();
        handler.setRemoteNotifier(notificationManager);

        server.setHandlers(0, handler.toValue());

        server.start();

        NotifierClientService client = new NotifierClientService();
        client.setNotificationInfoProvider(id -> info);
        client.setUri(new URI("ws://localhost:" + server.getPort() + "/notifier"));

        client.start();

        NotificationListener listener = results::add;

        client.addNotificationListener(1L, "some.string.event",
                listener);

        notificationManager.handleNotification(new Notification(1L, "some.string.event",
                1L, "Hello"));

        Notification notification = results.poll(2, TimeUnit.SECONDS);

        assertThat(notification, notNullValue());

        System.out.println(notification.getData());

        client.removeNotificationListener(1L, "some.string.event",
                listener);

        client.stop();

        server.stop();

    }
}
