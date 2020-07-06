package org.oddjob.http;

import org.junit.Test;
import org.oddjob.jetty.JettyHttpServer;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteInvoker;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ClientServerTest {

    public static class Fruit {

        private final String name;

        private final String colour;

        public Fruit(String name, String colour) {
            this.name = name;
            this.colour = colour;
        }

        public String getName() {
            return name;
        }

        public String getColour() {
            return colour;
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
    public void testInvoke() throws Exception {

        List<Object> returns = Arrays.asList(
                "Hello",
                true,
                new Fruit("Apple", "Red"),
                null);

        List<OperationType<?>> operationTypes = new ArrayList<>();
        List<Object[]> argsPassed = new ArrayList<>();

        AtomicInteger index = new AtomicInteger();

        RemoteInvoker invoker = new RemoteInvoker() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T invoke(long remoteId, OperationType<T> operationType, Object... args) {
                assertThat(remoteId, is(2L));

                operationTypes.add(operationType);
                argsPassed.add(args);

                return (T) returns.get(index.getAndIncrement());
            }
        };


        JettyHttpServer server = new JettyHttpServer();

        JettyInvokerHandler handler = new JettyInvokerHandler();
        handler.setRemoteInvoker(invoker);

        server.setHandlers(0, handler.toValue());

        server.start();

        OperationType<String> op1 = OperationType.ofName("greeting")
                .returning(String.class);
        OperationType<Boolean> op2 = OperationType.ofName("maybe")
                .withSignature(int.class, String.class).returning(boolean.class);
        OperationType<Fruit> op3 = OperationType.ofName("fruit")
                .withSignature(Fruit.class).returning(Fruit.class);
        OperationType<Void> op4 = OperationType.ofName("foo")
                .withSignature(String.class).returningVoid();

        try (InvokerClient client = new InvokerClient(
                new URI("http://localhost:" + server.getPort() + "/invoke")))
        {
            assertThat(client.invoke(2L, op1),
                    is("Hello"));
            assertThat(client.invoke(2L, op2, 42, "Foo"),
                    is(true));
            assertThat(client.invoke(2L, op3,
                    new Fruit("Pear", "Green")),
                    is(new Fruit("Apple", "Red")));
            assertThat(client.invoke(2L, op4,  new Object[] { null }),
                    nullValue(null));

        }

        assertThat(operationTypes.get(0), is( op1));
        assertThat(operationTypes.get(1), is( op2));
        assertThat(operationTypes.get(2), is( op3));
        assertThat(operationTypes.get(3), is( op4));

        assertThat(argsPassed.get(0).length, is(0) );
        assertThat(argsPassed.get(1)[0], is(42) );
        assertThat(argsPassed.get(1)[1], is("Foo") );
        assertThat(argsPassed.get(3)[0], nullValue());

        server.stop();
    }
}

