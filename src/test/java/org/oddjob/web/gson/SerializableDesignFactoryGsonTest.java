package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.mockito.Mockito;
import org.oddjob.arooa.parsing.SerializableDesignFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class SerializableDesignFactoryGsonTest {

    @Test
    public void testAdapter() {

        SerializableDesignFactory data =
                Mockito.mock(SerializableDesignFactory.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(
                        new SerializableDesignFactoryGson())
                .create();

        String json = gson.toJson(data);

        System.out.println(json);

        SerializableDesignFactory copy = gson.fromJson(json,
                SerializableDesignFactory.class);

        assertThat(copy, nullValue());

    }
}
