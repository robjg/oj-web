package org.oddjob.web.gson.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ClassResolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HierarchicalArrayGsonTest {

    @Test
    void testNumbers() {

        Gson gson = HierarchicalArrayGson
                .forHierarchicalArray(Number[].class, ClassResolver.getDefaultClassResolver()
                ).configure(new GsonBuilder())
                .create();

        Number[] numbers = { 1.2, 4, 5.2, 6 };

        String json = gson.toJson(numbers);

        Number[] copy = gson.fromJson(json, Number[].class);

        assertThat(copy, is(numbers));
    }
}