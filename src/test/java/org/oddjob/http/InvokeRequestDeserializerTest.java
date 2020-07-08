package org.oddjob.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.oddjob.remote.OperationType;

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class InvokeRequestDeserializerTest {

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
    public void testSerializeDeserializeReturningVoid() {

        OperationType<Void> ot = OperationType
                .ofName("foo")
                .withSignature(String.class, int.class, Fruit.class)
                .returningVoid();

        Fruit fruit = new Fruit();
        fruit.setName("Apple");
        fruit.setColour("Red");

        InvokeRequest invokeRequest = InvokeRequest.forRemoteId(1L)
                .withOperation(ot)
                .andArgs("Hello", 42, fruit);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OperationType.class, new OperationTypeDeSer(getClass().getClassLoader()))
                .registerTypeAdapter(InvokeRequest.class, new InvokeRequestDeserializer())
                .create();

        String json = gson.toJson(invokeRequest);

        System.out.println(json);

        InvokeRequest copy = gson.fromJson(json, InvokeRequest.class);

        assertThat(copy.getRemoteId(), is(1L));
        assertThat(copy.getOperationType(), is(ot));
        assertThat(copy.getArgs()[0], is("Hello"));
        assertThat(copy.getArgs()[1], is(42));
        assertThat(copy.getArgs()[2], is(fruit));

    }

    @Test
    public void testSerializeDeserializeArgAsArray() {

        OperationType<Void> ot = OperationType
                .ofName("foo")
                .withSignature(Object[].class)
                .returningVoid();

        Object[] arg0 = new Object[]{"Hello", 42};

        InvokeRequest invokeRequest = InvokeRequest.forRemoteId(1L)
                .withOperation(ot)
                .andArgs(new Object[] { arg0 });

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OperationType.class, new OperationTypeDeSer(getClass().getClassLoader()))
                .registerTypeAdapter(InvokeRequest.class, new InvokeRequestDeserializer())
                .create();

        String json = gson.toJson(invokeRequest);

        System.out.println(json);

        InvokeRequest copy = gson.fromJson(json, InvokeRequest.class);

        assertThat(copy.getRemoteId(), is(1L));
        assertThat(copy.getOperationType(), is(ot));
        assertThat( ((Object[]) copy.getArgs()[0])[0], is("Hello"));

        // We have no control over this...
        assertThat( ((Object[]) copy.getArgs()[0])[1], is(42.0));
    }

    @Test
    public void testSerializeDeserializeNullArg() {

        OperationType<Void> ot = OperationType
                .ofName("foo")
                .withSignature(String.class, String.class)
                .returningVoid();

        InvokeRequest invokeRequest = InvokeRequest.forRemoteId(1L)
                .withOperation(ot)
                .andArgs("Foo", null );

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OperationType.class, new OperationTypeDeSer(getClass().getClassLoader()))
                .registerTypeAdapter(InvokeRequest.class, new InvokeRequestDeserializer())
                .create();

        String json = gson.toJson(invokeRequest);

        System.out.println(json);

        InvokeRequest copy = gson.fromJson(json, InvokeRequest.class);

        assertThat(copy.getRemoteId(), is(1L));
        assertThat(copy.getOperationType(), is(ot));
        assertThat(copy.getArgs()[1], nullValue());

    }

    @Test
    public void testSerializeDeserializeWithNoArgs() {

        OperationType<Void> ot = OperationType
                .ofName("foo")
                .returningVoid();

        InvokeRequest invokeRequest = InvokeRequest.forRemoteId(1L)
                .withOperation(ot)
                .andNoArgs();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OperationType.class, new OperationTypeDeSer(getClass().getClassLoader()))
                .registerTypeAdapter(InvokeRequest.class, new InvokeRequestDeserializer())
                .create();

        String json = gson.toJson(invokeRequest);

        System.out.println(json);

        InvokeRequest copy = gson.fromJson(json, InvokeRequest.class);

        assertThat(copy.getRemoteId(), is(1L));
        assertThat(copy.getOperationType(), is(ot));
        assertThat(copy.getArgs(), nullValue());

    }
}