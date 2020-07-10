package org.oddjob.http;

import com.google.gson.*;
import org.oddjob.remote.OperationType;

import java.lang.reflect.Type;

/**
 * Gson deserializer for {@link InvokeRequest}s.
 */
public class InvokeRequestGson implements JsonSerializer<InvokeRequest>, JsonDeserializer<InvokeRequest> {

    public static final String REMOTE_ID = "remoteId";
    public static final String OPERATION_TYPE = "operationType";
    public static final String ARG_TYPES = "argTypes";
    public static final String ARGS = "args";

    @Override
    public JsonElement serialize(InvokeRequest src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(REMOTE_ID, src.getRemoteId());
        jsonObject.add(OPERATION_TYPE, context.serialize(src.getOperationType()));

        Object[] args = src.getArgs();
        if (args == null) {
            return jsonObject;
        }

        Class<?>[] signature = src.getOperationType().getSignature();
        Class<?>[] actualArgTypes = new Class[signature.length];
        boolean different = false;
        for (int i = 0; i < actualArgTypes.length; ++i) {
            if (signature[i].isPrimitive() ||
                    args[i] == null ||
                    args[i].getClass() == signature[i]) {
                actualArgTypes[i] = signature[i];
            }
            else {
                actualArgTypes[i] = args[i].getClass();
                different = true;
            }
        }
        if (different) {
            jsonObject.add(ARG_TYPES, context.serialize(actualArgTypes));
        }
        jsonObject.add(ARGS, context.serialize(args));

        return jsonObject;
    }

    @Override
    public InvokeRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        long remoteId = jsonObject.getAsJsonPrimitive(REMOTE_ID).getAsLong();

        OperationType<?> operationType = context.deserialize(
                jsonObject.getAsJsonObject(OPERATION_TYPE), OperationType.class);

        JsonArray argsArray = jsonObject.getAsJsonArray(ARGS);
        Object[] args;
        if (argsArray == null) {
            args = null;
        } else {
            JsonArray actualArray = jsonObject.getAsJsonArray(ARG_TYPES);
            Class<?>[] argTypes;
            if (actualArray == null) {
                argTypes = operationType.getSignature();
            }
            else {
                argTypes = context.deserialize(actualArray, Class[].class);
            }

            args = new Object[argsArray.size()];
            for (int i = 0; i < args.length; i++) {
                JsonElement element = argsArray.get(i);
                Class<?> argType = argTypes[i];
                args[i] = context.deserialize(element, argType);
            }
        }

        return new InvokeRequest(remoteId, operationType, args);
    }

}
