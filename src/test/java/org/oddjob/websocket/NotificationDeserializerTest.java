package org.oddjob.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationInfo;
import org.oddjob.remote.NotificationInfoBuilder;

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

        NotificationInfo info = new NotificationInfoBuilder()
                .addType("some.string.event").ofClass(String.class)
                .and()
                .addType("some.data.event").ofClass(UserData.class)
                .and()
                .addType("some.ints.event").ofClass(int[].class)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Notification.class, new NotificationDeserializer(id -> info))
                .create();

        Notification n1 = new Notification(1L,
                "some.string.event", 22L,
                "green");

        String json = gson.toJson(n1);

        Notification copy1 = gson.fromJson(json, Notification.class);

        assertThat(copy1.getRemoteId(), is(1L));
        assertThat(copy1.getType(), is("some.string.event"));
        assertThat(copy1.getData(), is("green"));

        Notification n2 = new Notification(1L,
                "some.data.event", 22L,
                new UserData("red", new long[]{1, 2, 3}));

        String json2 = gson.toJson(n2);

        Notification copy2 = gson.fromJson(json2, Notification.class);

        UserData ud = (UserData) copy2.getData();
        assertThat(ud.getaColour(), is("red"));
        assertThat(ud.getSomeNumbers(), is(new long[]{1L, 2L, 3L}));

        Notification n3 = new Notification(1L,
                "some.ints.event", 22L,
                new int[]{1, 2, 3});

        String json3 = gson.toJson(n3);

        Notification copy3 = gson.fromJson(json3, Notification.class);

        assertThat(copy3.getData(), is(new int[] {1, 2, 3}));
    }

    @Test
    public void testNullDataType() {

        NotificationInfo info = new NotificationInfoBuilder()
                .addType("some.void.event").ofClass(Void.class)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Notification.class, new NotificationDeserializer(id -> info))
                .create();

        Notification n1 = new Notification(1L,
                "some.void.event", 22L,
                null);

        String json = gson.toJson(n1);

        Notification copy1 = gson.fromJson(json, Notification.class);

        assertThat(copy1.getRemoteId(), is(1L));
        assertThat(copy1.getType(), is("some.void.event"));
        assertThat(copy1.getData(), nullValue());
    }

}
