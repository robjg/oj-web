package org.oddjob.websocket;

import org.junit.Test;
import org.oddjob.remote.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NotificationManagerTest {

    @Test
    public void testSubscribeUnsubscribe() throws RemoteException {

        NotificationInfo info = new NotificationInfoBuilder()
                .addType("some.string.event").ofClass(String.class)
                .build();

        List<String> subscribed = new ArrayList<>();
        List<String> unSubscribed = new ArrayList<>();

        NotificationManager test = new NotificationManager(
                id -> info,
                (remoteId, type) -> subscribed.add("" + remoteId + "-" + type),
                (remoteId, type) -> unSubscribed.add("" + remoteId + "-" + type));

        Notification n1 = new Notification(1L, "some.string.event", 1000L, "Hello");

        List<Notification> results = new ArrayList<>();

        NotificationListener listener = results::add;

        test.addNotificationListener(1L, "some.string.event",
                listener);

        assertThat(subscribed.size(), is(1));
        assertThat(subscribed.get(0), is("1-some.string.event"));

        test.handleNotification(n1);

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(n1));

        test.removeNotificationListener(1L, "some.string.event", listener);

        assertThat(unSubscribed.size(), is(1));
        assertThat(unSubscribed.get(0), is("1-some.string.event"));

        test.handleNotification(n1);

        assertThat(results.size(), is(1));

    }

}