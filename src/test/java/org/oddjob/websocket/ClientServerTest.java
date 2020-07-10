package org.oddjob.websocket;

import org.junit.Test;
import org.oddjob.jetty.JettyHttpServer;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationListener;
import org.oddjob.remote.NotificationType;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ClientServerTest {

    @Test
    public void testSubscribeReceiveUnsubscribe() throws Exception {

        BlockingQueue<Notification<String>> results = new LinkedBlockingDeque<>();

        NotificationManager notificationManager = new NotificationManager(
                (remoteId, type) -> {
                },
                (remoteId, type) -> {
                });

        JettyHttpServer server = new JettyHttpServer();

        JettyNotifierEndpointHandler handler = new JettyNotifierEndpointHandler();
        handler.setRemoteNotifier(notificationManager);

        server.setHandlers(0, handler.toValue());

        server.start();

        try (NotifierClient client = NotifierClient.create(
                new URI("ws://localhost:" + server.getPort() + "/notifier"),
                Runnable::run)) {

            NotificationListener<String> listener = results::add;

            NotificationType<String> notificationType =
                    NotificationType.ofName("some.string.event")
                            .andDataType(String.class);

            client.addNotificationListener(1L, notificationType, listener);

            notificationManager.handleNotification(new Notification<>(1L, notificationType,
                    1L, "Hello"));

            Notification<String> notification = results.poll(2, TimeUnit.SECONDS);

            assertThat(notification, notNullValue());

            assertThat(notification.getData(), is("Hello"));

            System.out.println(notification.getData());

            client.removeNotificationListener(1L, notificationType, listener);

        }

        server.stop();

    }
}
