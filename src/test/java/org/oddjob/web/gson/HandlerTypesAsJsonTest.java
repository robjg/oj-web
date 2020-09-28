package org.oddjob.web.gson;

import com.google.gson.Gson;
import org.junit.Test;
import org.oddjob.jmx.handlers.IconicHandlerFactory;
import org.oddjob.jmx.handlers.StatefulHandlerFactory;
import org.oddjob.jmx.handlers.StructuralHandlerFactory;
import org.oddjob.state.GenericState;
import org.oddjob.state.JobState;
import org.oddjob.state.State;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HandlerTypesAsJsonTest {

    @Test
    public void testStructural() {

        StructuralHandlerFactory.ChildData data = new StructuralHandlerFactory.ChildData(
                new long[] { 1, 2, 3, 4 });

        Gson gson = GsonUtil.createGson(getClass().getClassLoader());

        String json = gson.toJson(data);

        System.out.println(json);

        StructuralHandlerFactory.ChildData copy = gson.fromJson(json,
                StructuralHandlerFactory.ChildData.class);

        assertThat(copy.getRemoteIds(), is(data.getRemoteIds()));
    }

    @Test
    public void testStateful() {

        Date dataNow = new Date();

        State state = JobState.EXCEPTION;

        StatefulHandlerFactory.StateData data =
                new StatefulHandlerFactory.StateData(state, dataNow,
                        new RuntimeException("Ahhh!"));

        Gson gson = GsonUtil.createGson(getClass().getClassLoader());

        String json = gson.toJson(data);

        System.out.println(json);

        StatefulHandlerFactory.StateData copy  = gson.fromJson(json,
                StatefulHandlerFactory.StateData.class);

        assertThat(GenericState.statesEquivalent(state, data.getJobState()), is(true));
    }

    @Test
    public void testIconic() {

        IconicHandlerFactory.IconData data = new IconicHandlerFactory.IconData("running");

        Gson gson = GsonUtil.createGson(getClass().getClassLoader());

        String json = gson.toJson(data);

        System.out.println(json);

        IconicHandlerFactory.IconData copy  = gson.fromJson(json,
                IconicHandlerFactory.IconData.class);

        assertThat(copy.getIconId(), is("running"));
    }
}