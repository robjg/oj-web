package org.oddjob.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationListener;
import org.oddjob.remote.NotificationType;
import org.oddjob.remote.RemoteException;
import org.oddjob.tools.ManualClock;

import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class NotifierClientEndpointTest {

    @Test
    public void testSubscribeAndReceive() throws RemoteException, IOException {

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
        String subTypeJson = gson.toJson(NotifierServerEndpoint.ACTION_COMPLETE_TYPE);

        ScheduledExecutorService ses = mock(ScheduledExecutorService.class);
        doAnswer(inv -> {
            inv.getArgument(0, Runnable.class).run();
            return null;
        }).when(ses).execute(any(Runnable.class));

        NotifierClientEndpoint test = new NotifierClientEndpoint(ses);

        Session session = mock(Session.class);
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);

        doAnswer((Answer<Object>) invocationOnMock -> {
            String text = (String) invocationOnMock.getArguments()[0];
            String reply = "{\"remoteId\":-1,\"type\":" + subTypeJson + ",\"sequence\":0,\"data\":"
            + text + "}";
            new Thread(() -> test.onMessage(session, reply)).start();
            return null;
        }).when(basic).sendText(anyString());

        when(session.getBasicRemote()).thenReturn(basic);
        when(session.getId()).thenReturn("1234");

        EndpointConfig config = mock(EndpointConfig.class);
        when(config.getUserProperties()).thenReturn(new HashMap<>());

        // Open

        test.open(session, config);

        List<Notification<String>> results = new ArrayList<>();

        NotificationListener<String> listener = results::add;

        // Subscribe

        test.addNotificationListener(1L,
                stringType, listener);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(basic).sendText(captor.capture());

        assertThat(captor.getValue(), is(
                "{\"action\":\"ADD\",\"remoteId\":1,\"type\":" + stringTypeJson +"}"));

        // Receive

        test.onMessage(session, "{\"remoteId\":1,\"type\":" + stringTypeJson +
                ",\"sequence\":1000,\"data\":\"Hello\"}");

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(
                new Notification<>(1L, stringType, 1000L, "Hello")));

        // Unsubscribe

        test.removeNotificationListener(1L, stringType, listener);

        test.onMessage(session, "{\"remoteId\":1,\"type\":" + stringTypeJson + ",\"sequence\":1000,\"data\":\"Hello\"}");

        assertThat(results.size(), is(1));

        verify(basic, times(2)).sendText(captor.capture());

        assertThat(captor.getValue(), is(
                "{\"action\":\"REMOVE\",\"remoteId\":1,\"type\":" + stringTypeJson + "}"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHeartbeat() throws IOException {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NotificationType.class,
                        new NotificationTypeDesSer(getClass().getClassLoader()))
                .registerTypeAdapter(Notification.class,
                        new NotificationDeserializer())
                .create();

        ScheduledExecutorService ses = mock(ScheduledExecutorService.class);

        ManualClock clock = ManualClock
                .fromInstant(Instant.parse("2020-11-20T07:10:00Z"))
                .andSystemZone();

        NotifierClientEndpoint test = new NotifierClientEndpoint(ses, clock);

        Session session = mock(Session.class);
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);

        doAnswer((Answer<Object>) invocationOnMock -> {
            String text = (String) invocationOnMock.getArguments()[0];

            SubscriptionRequest request = gson.fromJson(text, SubscriptionRequest.class);

            Notification<SubscriptionRequest> notification =
                    new Notification<>(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                            NotifierServerEndpoint.ACTION_COMPLETE_TYPE, 0L,
                            request);

            String reply = gson.toJson(notification);
            new Thread(() -> test.onMessage(session, reply)).start();
            return null;
        }).when(basic).sendText(anyString());

        when(session.getBasicRemote()).thenReturn(basic);
        when(session.getId()).thenReturn("1234");

        @SuppressWarnings("rawtypes")
        ScheduledFuture future = mock(ScheduledFuture.class);

        when(ses.scheduleWithFixedDelay(
                any(Runnable.class),
                eq(NotifierClientEndpoint.HEARTBEAT_SECONDS),
                eq(NotifierClientEndpoint.HEARTBEAT_SECONDS),
                eq(TimeUnit.SECONDS))).thenReturn(future);

        EndpointConfig endpointConfig = mock(EndpointConfig.class);

        // Open Session

        test.open(session, endpointConfig);

        ArgumentCaptor<Runnable> captureSubmit = ArgumentCaptor.forClass(Runnable.class);

        verify(ses, times(1)).scheduleWithFixedDelay(
                captureSubmit.capture(),
                eq(NotifierClientEndpoint.HEARTBEAT_SECONDS),
                eq(NotifierClientEndpoint.HEARTBEAT_SECONDS),
                eq(TimeUnit.SECONDS));

        // Simulate heartbeat seconds elapsed with no messages

        captureSubmit.getValue().run();

        // Heartbeat should happen
        verify(basic).sendText(anyString());

        // And then heartbeat under heartbeat seconds

        clock.setInstant(Instant.parse("2020-11-20T07:14:00Z"));

        Notification<Void> dummyNotification =
                new Notification<>(NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                        NotificationType.ofName("dummy").andNoData(), 0L);

        test.onMessage(session, gson.toJson(dummyNotification));

        captureSubmit.getValue().run();

        verifyNoMoreInteractions(basic);

        // And close

        test.close(session);

        verify(future, times(1)).cancel(false);
    }
}