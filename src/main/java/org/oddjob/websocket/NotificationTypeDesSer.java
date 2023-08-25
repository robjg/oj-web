package org.oddjob.websocket;

import com.google.gson.*;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.remote.NotificationType;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Gson serializer and deserializer for {@link NotificationType}s.
 */
public class NotificationTypeDesSer implements JsonSerializer<NotificationType<?>>, JsonDeserializer<NotificationType<?>> {

    public static final String NAME = "name";
    public static final String TYPE = "type";

    private final ClassResolver classResolver;

    public NotificationTypeDesSer(ClassResolver classResolver) {
        this.classResolver = classResolver;
    }

    @Override
    public JsonElement serialize(NotificationType<?> src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(NAME, src.getName());
        jsonObject.addProperty(TYPE, src.getDataType().getName());

        return jsonObject;
    }

    @Override
    public NotificationType<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = (JsonObject) json;

        String name = jsonObject.getAsJsonPrimitive(NAME).getAsString();

        String className = jsonObject.getAsJsonPrimitive(TYPE).getAsString();
        //noinspection unchecked
        Class<? extends Serializable> type = (Class<? extends Serializable>) classResolver.findClass(className);
        if (type == null) {
            throw new JsonParseException("Class not found " + className);
        }

        return new NotificationType<>(name, type);
    }
}
