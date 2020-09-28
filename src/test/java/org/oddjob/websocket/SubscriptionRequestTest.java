package org.oddjob.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubscriptionRequestTest {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionRequestTest.class);
    @Test
    public void testJson() {

        NotificationType<String> stringType =
                NotificationType.ofName("some.string.event")
                        .andDataType(String.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NotificationType.class,
                        new NotificationTypeDesSer(getClass().getClassLoader()))
                .create();

        SubscriptionRequest test = new SubscriptionRequest();
        test.setAction(SubscriptionRequest.Action.REMOVE);
        test.setRemoteId(42L);
        test.setType(stringType);

        String json = gson.toJson(test);

        logger.debug(json);

        SubscriptionRequest copy = gson.fromJson(json, SubscriptionRequest.class);

        assertThat(copy.getAction(), is(SubscriptionRequest.Action.REMOVE));
        assertThat(copy.getRemoteId(), is(42L));
        assertThat(copy.getType(), is(stringType));
    }

    @Test
    public void testSubscriptionAcknowledge() {

        NotificationType<String> stringType =
                NotificationType.ofName("some.string.event")
                        .andDataType(String.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NotificationType.class,
                        new NotificationTypeDesSer(getClass().getClassLoader()))
                .registerTypeAdapter(Notification.class,
                        new NotificationDeserializer())
                .create();

        SubscriptionRequest test = new SubscriptionRequest();
        test.setAction(SubscriptionRequest.Action.REMOVE);
        test.setRemoteId(42L);
        test.setType(stringType);

        Notification<SubscriptionRequest> ack = new Notification<>(
                NotifierServerEndpoint.SYSTEM_REMOTE_ID,
                NotifierServerEndpoint.ACTION_COMPLETE_TYPE,
                0L,
                test);

        String json = gson.toJson(ack);

        System.out.println(gson.toJson(ack));

        Notification<SubscriptionRequest> copy =
                gson.fromJson(json, new TypeToken<Notification<SubscriptionRequest>>() {}.getType());

        SubscriptionRequest reqCopy = copy.getData();

        assertThat(reqCopy.getType(), is(stringType));
        assertThat(reqCopy.getRemoteId(), is(42L));
        assertThat(reqCopy.getAction(), is(SubscriptionRequest.Action.REMOVE));
    }
}