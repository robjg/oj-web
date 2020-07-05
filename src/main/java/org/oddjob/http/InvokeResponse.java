package org.oddjob.http;

/**
 * Response sent back from server to client.
 *
 * @param <T> The type of the response.
 */
public class InvokeResponse<T> {

    private final Class<T> type;

    private final T value;

    public InvokeResponse(Class<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "InvokeResponse{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }

    public static <T> InvokeResponse<T> from(Class<T> type, T value) {
        return new InvokeResponse<>(type, value);
    }
}
