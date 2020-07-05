package org.oddjob.http;

import com.google.gson.*;
import org.oddjob.remote.OperationType;

import java.lang.reflect.Type;

/**
 * Gson deserializer for {@link InvokeRequest}s.
 */
public class InvokeRequestDeserializer implements JsonDeserializer<InvokeRequest> {

    public static final String REMOTE_ID = "remoteId";
    public static final String OPERATION_TYPE = "operationType";
    public static final String ARGS = "args";

    @Override
    public InvokeRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        long remoteId = jsonObject.getAsJsonPrimitive(REMOTE_ID).getAsLong();

        OperationType<?> operationType = context.deserialize(
                jsonObject.getAsJsonObject(OPERATION_TYPE), OperationType.class);

        JsonArray argsArray = jsonObject.getAsJsonArray(ARGS);
        Object[] args = new Object[argsArray.size()];
        for (int i = 0; i < args.length; i++) {
            JsonElement element = argsArray.get(i);
            Class<?> argType = operationType.getSignature()[i];
            args[i] = context.deserialize(element, argType);
        }

        return new InvokeRequest(remoteId, operationType, args);
    }

}
