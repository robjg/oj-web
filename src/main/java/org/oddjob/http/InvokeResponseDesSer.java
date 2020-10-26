package org.oddjob.http;

import com.google.gson.*;
import org.oddjob.arooa.utils.ClassUtils;

import java.lang.reflect.Type;

/**
 * Serializer and Deserializer for an {@link InvokeResponse}.
 */
public class InvokeResponseDesSer implements JsonSerializer<InvokeResponse<?>>, JsonDeserializer<InvokeResponse<?>> {

    public static final String TYPE = "type";
    public static final String VALUE = "value";

    private final ClassLoader classLoader;

    public InvokeResponseDesSer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public JsonElement serialize(InvokeResponse<?> src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, src.getType().getName());

        if (src.getType() != void.class && src.getType() != Void.class) {
            jsonObject.add(VALUE, context.serialize(src.getValue(), src.getType()));
        }

        return jsonObject;
    }

    @Override
    public InvokeResponse<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        Class<?> type;
        try {
            type = ClassUtils.classFor(jsonObject.getAsJsonPrimitive(TYPE).getAsString(), classLoader);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }

        JsonElement valueJson = jsonObject.get(VALUE);

        if (valueJson == null) {
            return new InvokeResponse<>(type, null );
        }
        else {
            return InvokeResponse.from(type, context.deserialize(valueJson, type));
        }
    }
}
