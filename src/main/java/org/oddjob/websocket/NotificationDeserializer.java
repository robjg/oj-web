package org.oddjob.websocket;

import com.google.gson.*;
import org.oddjob.remote.Notification;
import org.oddjob.remote.NotificationInfo;
import org.oddjob.remote.NotificationInfoProvider;
import org.oddjob.remote.RemoteException;

import java.lang.reflect.Type;

/**
 * Gson deserializer for {@link Notification}s. A {@link NotificationInfoProvider} is required
 * to deserialize the user data.
 */
public class NotificationDeserializer implements JsonDeserializer<Notification> {

    public static final String TYPE = "type";
    public static final String SEQUENCE = "sequence";
    public static final String REMOTE_ID = "remoteId";
    public static final String DATA = "data";

    private final NotificationInfoProvider notificationInfoProvider;

    public NotificationDeserializer(NotificationInfoProvider notificationInfoProvider) {
        this.notificationInfoProvider = notificationInfoProvider;
    }

    @Override
    public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        long remoteId = jsonObject.getAsJsonPrimitive(REMOTE_ID).getAsLong();
        String type = jsonObject.getAsJsonPrimitive(TYPE).getAsString();
        long seq = jsonObject.getAsJsonPrimitive(SEQUENCE).getAsLong();

        JsonElement ud = jsonObject.get(DATA);

        NotificationInfo notificationInfo;
        try {
            notificationInfo = notificationInfoProvider.getNotificationInfo(remoteId);
        } catch (RemoteException e) {
            throw new JsonParseException("Failed getting notification info for " + remoteId, e);
        }

        if (notificationInfo == null) {
            throw new JsonParseException("No notification info for " + remoteId);
        }

        Class<?> dataType = notificationInfo.getTypeOf(type);

        if (dataType == null) {
            throw new JsonParseException("No notification type info for " + remoteId + " and " + type);
        }

        Object notificationData = context.deserialize(ud, dataType);

        return new Notification(remoteId, type, seq, notificationData);
    }

}
