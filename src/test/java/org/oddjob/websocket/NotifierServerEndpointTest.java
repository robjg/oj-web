package org.oddjob.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationType;
import org.oddjob.remote.RemoteException;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class NotifierServerEndpointTest {


    @Test
    public void givenSubscribeUnsubscribeSubscribeWhenNotificationsThenSentAsExpected()
            throws RemoteException, IOException {

        NotificationType<String> stringType =
                NotificationType.ofName("some.string.event")
                        .andDataType(String.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NotificationType.class,
                        new NotificationTypeDesSer(getClass().getClassLoader()))
                .registerTypeAdapter(Notification.class,
                        new NotificationDeserializer())
                .create();

        String stringTypeJson = gson.toJson(stringType);

        List<String> subscribed = new ArrayList<>();
        List<String> unSubscribed = new ArrayList<>();

        // Providing a real notification manager with a fake RemoteNotifier
        NotificationManager notificationManager = new NotificationManager(
                (remoteId, type) -> subscribed.add("" + remoteId + "-" + type),
                (remoteId, type) -> unSubscribed.add("" + remoteId + "-" + type));

        // Mock the session
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);

        Session session = mock(Session.class);
        when(session.getBasicRemote()).thenReturn(basic);
        when(session.getId()).thenReturn("1234");

        // Subject Under Test
        NotifierServerEndpoint test = new NotifierServerEndpoint(notificationManager);

        // Subscribe

        test.onMessage(session, "{\"action\":\"ADD\",\"remoteId\":1,\"type\":" + stringTypeJson + "}");

        assertThat(subscribed.size(), is(1));

        // Send

        Notification<String> n1 = new Notification<>(1L, stringType, 1000L, "Hello");

        notificationManager.handleNotification(n1);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(basic, times(1)).sendText(captor.capture());

        assertThat(captor.getValue(), is(
                "{\"remoteId\":1,\"type\":" + stringTypeJson + ",\"sequence\":1000,\"data\":\"Hello\"}"));

        // Unsubscribe

        test.onMessage(session, "{\"action\":\"REMOVE\",\"remoteId\":1,\"type\":" + stringTypeJson + "}");

        assertThat(unSubscribed.size(), is(1));

        // Send Again

        Notification<String> n2 = new Notification<>(1L, stringType, 1001L, "You'll Miss this");

        notificationManager.handleNotification(n2);

        verifyNoMoreInteractions(basic);

        // Subscribe Again

        test.onMessage(session, "{\"action\":\"ADD\",\"remoteId\":1,\"type\":" + stringTypeJson + "}");

        assertThat(subscribed.size(), is(2));

        // Send

        Notification<String> n3 = new Notification<>(1L, stringType, 1002L, "Hello Again");

        notificationManager.handleNotification(n3);

        ArgumentCaptor<String> captor3 = ArgumentCaptor.forClass(String.class);

        verify(basic, times(2)).sendText(captor3.capture());

        assertThat(captor3.getValue(), is(
                "{\"remoteId\":1,\"type\":" + stringTypeJson + ",\"sequence\":1002,\"data\":\"Hello Again\"}"));
    }
}