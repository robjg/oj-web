package org.oddjob.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class NotificationDeserializerTest {

    public static class UserData {

        private final String aColour;

        private final long[] someNumbers;


        public UserData(String aColour, long[] someNumbers) {
            this.aColour = aColour;
            this.someNumbers = someNumbers;
        }

        public String getaColour() {
            return aColour;
        }

        public long[] getSomeNumbers() {
            return someNumbers;
        }
    }


    @Test
    public void testNotificationFromTo() {

        NotificationType<String> stringType =
                NotificationType.ofName("some.string.event")
                        .andDataType(String.class);

        NotificationType<UserData> dataType =
                NotificationType.ofName("some.data.event")
                                .andDataType(UserData.class);

        NotificationType<int[]> intsType =
                NotificationType.ofName("some.ints.event")
                        .andDataType(int[].class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NotificationType.class,
                        new NotificationTypeDesSer(getClass().getClassLoader()))
                .registerTypeAdapter(Notification.class,
                        new NotificationDeserializer())
                .create();

        Notification<String> n1 = new Notification<>(1L,
                stringType, 22L,
                "green");

        String json = gson.toJson(n1);

        Notification<String> copy1 = gson.fromJson(json,
                new TypeToken<Notification<String>>(){}.getType());

        assertThat(copy1.getRemoteId(), is(1L));
        assertThat(copy1.getType().getName(), is("some.string.event"));
        assertThat(copy1.getData(), is("green"));

        Notification n2 = new Notification(1L,
                dataType, 22L,
                new UserData("red", new long[]{1, 2, 3}));

        String json2 = gson.toJson(n2);

        Notification copy2 = gson.fromJson(json2, Notification.class);

        UserData ud = (UserData) copy2.getData();
        assertThat(ud.getaColour(), is("red"));
        assertThat(ud.getSomeNumbers(), is(new long[]{1L, 2L, 3L}));

        Notification n3 = new Notification(1L,
                intsType, 22L,
                new int[]{1, 2, 3});

        String json3 = gson.toJson(n3);

        Notification copy3 = gson.fromJson(json3, Notification.class);

        assertThat(copy3.getData(), is(new int[] {1, 2, 3}));
    }

    @Test
    public void testNullDataType() {

        NotificationType<Void> notificationType =
                NotificationType.ofName("some.void.event")
                        .andDataType(void.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NotificationType.class,
                        new NotificationTypeDesSer(getClass().getClassLoader()))
                .registerTypeAdapter(Notification.class,
                        new NotificationDeserializer())
                .create();

        Notification n1 = new Notification(1L,
                notificationType, 22L,
                null);

        String json = gson.toJson(n1);

        Notification<Void> copy1 = gson.fromJson(json,
                new TypeToken<Notification<Void>>(){}.getType());

        assertThat(copy1.getRemoteId(), is(1L));
        assertThat(copy1.getType(), is(notificationType));
        assertThat(copy1.getData(), nullValue());
    }

}
