package org.oddjob.websocket;

import com.google.gson.Gson;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubscriptionRequestTest {

    @Test
    public void testJson() {

        Gson gson = new Gson();

        SubscriptionRequest test = new SubscriptionRequest();
        test.setAction(SubscriptionRequest.Action.REMOVE);
        test.setRemoteId(42L);
        test.setType("my.events");

        SubscriptionRequest copy = gson.fromJson(gson.toJson(test), SubscriptionRequest.class);

        assertThat(copy.getAction(), is(SubscriptionRequest.Action.REMOVE));
        assertThat(copy.getRemoteId(), is(42L));
        assertThat(copy.getType(), is("my.events"));
    }
}