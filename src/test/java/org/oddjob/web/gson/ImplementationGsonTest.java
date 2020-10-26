package org.oddjob.web.gson;

import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.remote.Implementation;
import org.oddjob.remote.Initialisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ImplementationGsonTest {

    private static final Logger logger = LoggerFactory.getLogger(ImplementationGsonTest.class);

    @Test
    public void testSerializeDeserializeWithInitialisation() {

        Implementation<?> test = Implementation.create(Object.class.getName(), "2.0",
                Initialisation.from(String.class, "Some Thing"));

        Gson gson = GsonUtil.createGson(getClass().getClassLoader());

        String json = gson.toJson(test);

        logger.info(json);

        Implementation<?> copy = gson.fromJson(json, Implementation.class);

        assertThat(copy.getType(), is(test.getType()));
        assertThat(copy.getVersion(), is(test.getVersion()));
        assertThat(copy.getInitialisation().getType(), is(test.getInitialisation().getType()));
        assertThat(copy.getInitialisation().getData(), is(test.getInitialisation().getData()));
    }

    @Test
    public void testSerializeDeserializeNoInitialisation() {

        Implementation<?> test = Implementation.create(Object.class.getName(), "2.0");

        Gson gson = GsonUtil.createGson(getClass().getClassLoader());

        String json = gson.toJson(test);

        logger.info(json);

        Implementation<?> copy = gson.fromJson(json, Implementation.class);

        assertThat(copy.getType(), is(test.getType()));
        assertThat(copy.getVersion(), is(test.getVersion()));
        assertThat(copy.getInitialisation(), Matchers.nullValue());
    }

}