package org.oddjob.websocket;

import org.junit.Test;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationListener;
import org.oddjob.remote.NotificationType;
import org.oddjob.remote.RemoteException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NotificationManagerTest {

    @Test
    public void testSubscribeUnsubscribe() throws RemoteException {

        NotificationType<String> stringType =
                NotificationType.ofName("some.string.event")
                        .andDataType(String.class);

        List<String> subscribed = new ArrayList<>();
        List<String> unSubscribed = new ArrayList<>();

        NotificationManager test = new NotificationManager(
                (remoteId, type) -> subscribed.add("" + remoteId + "-" + type.getName()),
                (remoteId, type) -> unSubscribed.add("" + remoteId + "-" + type.getName()));

        Notification<String> n1 = new Notification<>(1L, stringType, 1000L, "Hello");

        List<Notification<String>> results = new ArrayList<>();

        NotificationListener<String> listener = results::add;

        test.addNotificationListener(1L, stringType, listener);

        assertThat(subscribed.size(), is(1));
        assertThat(subscribed.get(0), is("1-some.string.event"));

        test.handleNotification(n1);

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(n1));

        test.removeNotificationListener(1L, stringType, listener);

        assertThat(unSubscribed.size(), is(1));
        assertThat(unSubscribed.get(0), is("1-some.string.event"));

        test.handleNotification(n1);

        assertThat(results.size(), is(1));

    }

}