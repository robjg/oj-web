package org.oddjob.web.gson;

import com.google.gson.Gson;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ClassAdapterTest {

    @Test
    public void testAdapter() {

        Class<?> data = String.class;

        Gson gson = GsonUtil.createGson(getClass().getClassLoader());

        String json = gson.toJson(data);

        System.out.println(json);

        Class<?> copy = gson.fromJson(json,
                Class.class);

        assertThat(copy, is(String.class));


    }
}
