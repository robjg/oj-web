package org.oddjob.web.gson;

import com.google.gson.Gson;
import org.junit.Test;
import org.oddjob.jmx.handlers.StructuralHandlerFactory;

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
}
