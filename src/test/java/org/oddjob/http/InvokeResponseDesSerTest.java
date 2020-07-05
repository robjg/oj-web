package org.oddjob.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class InvokeResponseDesSerTest {

    public static class Fruit {

        private String name;

        private String colour;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColour() {
            return colour;
        }

        public void setColour(String colour) {
            this.colour = colour;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Fruit fruit = (Fruit) o;
            return Objects.equals(name, fruit.name) &&
                    Objects.equals(colour, fruit.colour);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, colour);
        }
    }

    @Test
    public void testSerializeDeserializeObject() {

        Fruit fruit = new Fruit();
        fruit.setName("Apple");
        fruit.setColour("Red");

        InvokeResponse<Fruit> invokeResponse =
                new InvokeResponse<>(Fruit.class, fruit);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(InvokeResponse.class,
                        new InvokeResponseDesSer(getClass().getClassLoader()))
                .create();

        String json = gson.toJson(invokeResponse);

        System.out.println(json);

        InvokeResponse copy = gson.fromJson(json, InvokeResponse.class);

        assertThat(copy.getType(), is(Fruit.class));
        assertThat(copy.getValue(), is(fruit));

    }

    @Test
    public void testSerializeDeserializeNullObject() {

        InvokeResponse<Fruit> invokeResponse =
                new InvokeResponse<>(Fruit.class, null);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(InvokeResponse.class,
                        new InvokeResponseDesSer(getClass().getClassLoader()))
                .create();

        String json = gson.toJson(invokeResponse);

        System.out.println(json);

        InvokeResponse copy = gson.fromJson(json, InvokeResponse.class);

        assertThat(copy.getType(), is(Fruit.class));
        assertThat(copy.getValue(), nullValue());

    }

    @Test
    public void testInt() {

        InvokeResponse<Integer> invokeResponse =
                new InvokeResponse<>(int.class, 42);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(InvokeResponse.class,
                        new InvokeResponseDesSer(getClass().getClassLoader()))
                .create();

        String json = gson.toJson(invokeResponse);

        System.out.println(json);

        InvokeResponse copy = gson.fromJson(json, InvokeResponse.class);

        assertThat(copy.getType(), is(int.class));
        assertThat(copy.getValue(), is(42));
    }

    @Test
    public void testVoid() {

        InvokeResponse<Void> invokeResponse =
                new InvokeResponse<>(void.class, null);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(InvokeResponse.class,
                        new InvokeResponseDesSer(getClass().getClassLoader()))
                .create();

        String json = gson.toJson(invokeResponse);

        System.out.println(json);

        InvokeResponse copy = gson.fromJson(json, InvokeResponse.class);

        assertThat(copy.getType(), is(void.class));
        assertThat(copy.getValue(), nullValue());
    }
}