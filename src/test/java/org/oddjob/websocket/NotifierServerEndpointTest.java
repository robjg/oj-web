package org.oddjob.websocket;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationInfo;
import org.oddjob.remote.NotificationInfoBuilder;
import org.oddjob.remote.RemoteException;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class NotifierServerEndpointTest {


    @Test
    public void test() throws RemoteException, IOException {

        NotificationInfo info = new NotificationInfoBuilder()
                .addType("some.string.event").ofClass(String.class)
                .build();

        List<String> subscribed = new ArrayList<>();
        List<String> unSubscribed = new ArrayList<>();

        NotificationManager notificationManager = new NotificationManager(
                id -> info,
                (remoteId, type) -> subscribed.add("" + remoteId + "-" + type),
                (remoteId, type) -> unSubscribed.add("" + remoteId + "-" + type));

        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);

        Session session = mock(Session.class);
        when(session.getBasicRemote()).thenReturn(basic);
        when(session.getId()).thenReturn("1234");

        NotifierServerEndpoint test = new NotifierServerEndpoint(notificationManager);
        test.onMessage(session, "{\"action\":\"ADD\",\"remoteId\":1,\"type\":\"some.string.event\"}");

        assertThat(subscribed.size(), is(1));

        Notification n1 = new Notification(1L, "some.string.event", 1000L, "Hello");

        notificationManager.handleNotification(n1);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(basic).sendText(captor.capture());

        assertThat(captor.getValue(), is(
                "{\"remoteId\":1,\"type\":\"some.string.event\",\"sequence\":1000,\"data\":\"Hello\"}"));

        test.onMessage(session, "{\"action\":\"REMOVE\",\"remoteId\":1,\"type\":\"some.string.event\"}");

        assertThat(unSubscribed.size(), is(1));
    }
}