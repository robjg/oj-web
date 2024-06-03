package org.oddjob.web.gson.plugin;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.jmx.handlers.StatefulHandlerFactory;
import org.oddjob.state.JobState;
import org.oddjob.state.StateInstant;
import org.oddjob.state.StateInstantClock;
import org.oddjob.web.gson.GsonUtil;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StateDataGsonTest {

    @Test
    void serializeDeserialize() {

        Gson gson = GsonUtil.createGson(new StandardArooaSession());

        StateInstant stateInstant = StateInstant.now(StateInstantClock.fromClock(
                Clock.fixed(Instant.parse("2024-05-29T21:30:07.640740500Z"), ZoneOffset.UTC), () -> 0L));

        StatefulHandlerFactory.StateData stateData1 = new StatefulHandlerFactory.StateData(
                JobState.EXCEPTION, stateInstant, new Exception("An Exception", new UnknownError("Because of This")));

        String json = gson.toJson(stateData1);

        System.out.println(json);

        StatefulHandlerFactory.StateData stateData = gson.fromJson(json, StatefulHandlerFactory.StateData.class);

        assertThat(stateData.getState().isException(), is(true));
        assertThat(stateData.getStateInstant(), is(stateInstant));
        assertThat(stateData.getStateInstant(), is(stateInstant));
        assertThat(stateData.getException().toString(), is("java.lang.Exception: An Exception"));
        assertThat(stateData.getException().getCause().toString(), is("java.lang.UnknownError: Because of This"));


    }

}