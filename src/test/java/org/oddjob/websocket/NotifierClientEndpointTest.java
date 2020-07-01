package org.oddjob.websocket;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.oddjob.remote.*;

import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class NotifierClientEndpointTest {

    @Test
    public void testSubscribeAndReceive() throws RemoteException, IOException {

        NotificationInfo info = new NotificationInfoBuilder()
                .addType(NotifierServerEndpoint.ACTION_COMPLETE_TYPE).ofClass(SubscriptionRequest.class)
                .and()
                .addType("some.string.event").ofClass(String.class)
                .build();

        NotifierClientEndpoint test = new NotifierClientEndpoint(id -> info);

        Session session = mock(Session.class);
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);

        doAnswer((Answer<Object>) invocationOnMock -> {
            String text = (String) invocationOnMock.getArguments()[0];
            String reply = "{\"remoteId\":-1,\"type\":\"notifier.action.complete\",\"sequence\":0,\"data\":" +
                    text + "}";
            new Thread(() -> {test.onMessage(session, reply);}).start();
            return null;
        }).when(basic).sendText(anyString());

        when(session.getBasicRemote()).thenReturn(basic);
        when(session.getId()).thenReturn("1234");

        EndpointConfig config = mock(EndpointConfig.class);
        when(config.getUserProperties()).thenReturn(new HashMap<>());

        // Open

        test.open(session, config);

        List<Notification> results = new ArrayList<>();

        NotificationListener listener = results::add;

        // Subscribe

        test.addNotificationListener(1L,
                "some.string.event", listener);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(basic).sendText(captor.capture());

        assertThat(captor.getValue(), is(
                "{\"action\":\"ADD\",\"remoteId\":1,\"type\":\"some.string.event\"}"));

        // Receive

        test.onMessage(session, "{\"remoteId\":1,\"type\":\"some.string.event\",\"sequence\":1000,\"data\":\"Hello\"}");

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(
                new Notification(1L, "some.string.event", 1000L, "Hello")));

        // Unsubscribe

        test.removeNotificationListener(1L, "some.string.event", listener);

        test.onMessage(session, "{\"remoteId\":1,\"type\":\"some.string.event\",\"sequence\":1000,\"data\":\"Hello\"}");

        assertThat(results.size(), is(1));

        verify(basic, times(2)).sendText(captor.capture());

        assertThat(captor.getValue(), is(
                "{\"action\":\"REMOVE\",\"remoteId\":1,\"type\":\"some.string.event\"}"));
    }
}