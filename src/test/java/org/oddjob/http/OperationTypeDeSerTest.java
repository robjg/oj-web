package org.oddjob.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.remote.OperationType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class OperationTypeDeSerTest {

    @Test
    public void testSerializeDeserialize() {

        OperationType<Void> ot = OperationType
                .ofName("foo")
                .withSignature(String.class, int.class)
                .returning(void.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OperationType.class, new OperationTypeDeSer(ClassResolver.getDefaultClassResolver()))
                .create();

        String json = gson.toJson(ot);

        System.out.println(json);

        OperationType<?> copy = gson.fromJson(json, OperationType.class);

        assertThat(copy, is(ot));
    }
}