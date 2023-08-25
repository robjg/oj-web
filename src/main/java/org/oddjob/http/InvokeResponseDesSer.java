package org.oddjob.http;

import com.google.gson.*;
import org.oddjob.arooa.ClassResolver;

import java.lang.reflect.Type;

/**
 * Serializer and Deserializer for an {@link InvokeResponse}.
 */
public class InvokeResponseDesSer implements JsonSerializer<InvokeResponse<?>>, JsonDeserializer<InvokeResponse<?>> {

    public static final String TYPE = "type";
    public static final String VALUE = "value";

    private final ClassResolver classResolver;

    public InvokeResponseDesSer(ClassResolver classResolver) {
        this.classResolver = classResolver;
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

        String className = jsonObject.getAsJsonPrimitive(TYPE).getAsString();
        //noinspection unchecked
        Class<?> type = classResolver.findClass(className);
        if (type == null) {
            throw new JsonParseException("Class not found: " + className);
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
