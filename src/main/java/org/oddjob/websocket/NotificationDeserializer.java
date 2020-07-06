package org.oddjob.websocket;

import com.google.gson.*;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationType;

import java.lang.reflect.Type;

/**
 * Gson deserializer for {@link Notification}s.
 */
public class NotificationDeserializer implements JsonDeserializer<Notification<?>> {

    public static final String TYPE = "type";
    public static final String SEQUENCE = "sequence";
    public static final String REMOTE_ID = "remoteId";
    public static final String DATA = "data";

    public NotificationDeserializer() {
    }

    @Override
    public Notification<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        long remoteId = jsonObject.getAsJsonPrimitive(REMOTE_ID).getAsLong();

        NotificationType<?> type = context.deserialize(jsonObject.get(TYPE), NotificationType.class);

        long seq = jsonObject.getAsJsonPrimitive(SEQUENCE).getAsLong();

        JsonElement ud = jsonObject.get(DATA);

        if (ud == null) {
            return new Notification<>(remoteId, type, seq, null);
        }
        else {
            return inferNotificationType(remoteId, type, seq, ud, context);
        }
    }

    <T> Notification<T> inferNotificationType(
            long remoteId, NotificationType<T> type, long seq, JsonElement ud, JsonDeserializationContext context) {

        T data = context.deserialize(ud, type.getDataType());
        return new Notification<>(remoteId, type, seq, ClassUtils.cast(type.getDataType(), data));
    }
}
