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
        int argsLength = args == null ? 0 : args.length;
        Class<?>[] signature = src.getOperationType().getSignature();
        if (argsLength != signature.length) {
            throw new IllegalArgumentException("Args length differ from signature length: "
                    + argsLength + "!=" + signature.length);
        }
        if (argsLength == 0) {
            return jsonObject;
        }

        // When args type differ from signature because they are
        // subclasses we also pass the actual types.
        Class<?>[] actualArgTypes = new Class[signature.length];
        boolean different = false;
        for (int i = 0; i < actualArgTypes.length; ++i) {
            Class<?> signatureClass = signature[i];
            Class<?> argClass = args[i] == null ? null : args[i].getClass();
            if (signatureClass.isPrimitive() ||
                    argClass == null ||
                    argClass == signatureClass) {
                actualArgTypes[i] = signature[i];
            } else {
                actualArgTypes[i] = argClass;
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
            } else {
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
