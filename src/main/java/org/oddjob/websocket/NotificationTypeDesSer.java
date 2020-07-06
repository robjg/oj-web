package org.oddjob.websocket;

import com.google.gson.*;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.remote.NotificationType;

import java.lang.reflect.Type;

/**
 * Gson serializer and deserializer for {@link NotificationType}s.
 */
public class NotificationTypeDesSer implements JsonSerializer<NotificationType<?>>, JsonDeserializer<NotificationType<?>> {

    public static final String NAME = "name";
    public static final String TYPE = "type";

    private final ClassLoader classLoader;

    public NotificationTypeDesSer(ClassLoader classLoader) {
        this.classLoader = classLoader;
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
        Class<?> type;
        try {
            type = ClassUtils.classFor(jsonObject.getAsJsonPrimitive(TYPE).getAsString(), classLoader);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }

        return new NotificationType<>(name, type);
    }
}
