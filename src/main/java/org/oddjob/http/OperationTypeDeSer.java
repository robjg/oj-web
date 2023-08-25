package org.oddjob.http;

import com.google.gson.*;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.remote.OperationType;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * Gson deserializer for {@link OperationType}s.
 */
public class OperationTypeDeSer implements JsonSerializer<OperationType<?>>, JsonDeserializer<OperationType<?>> {

    public static final String NAME = "name";
    public static final String SIGNATURE = "signature";
    public static final String TYPE = "type";

    private final ClassResolver classResolver;

    public OperationTypeDeSer(ClassResolver classResolver) {
        this.classResolver = classResolver;
    }

    @Override
    public JsonElement serialize(OperationType<?> src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(NAME, src.getName());
        jsonObject.addProperty(TYPE, src.getReturnType().getName());

        JsonArray signature = new JsonArray();
        Arrays.stream(src.getSignature()).forEach(cl -> signature.add(cl.getName()));

        jsonObject.add(SIGNATURE, signature);

        return jsonObject;
    }

    @Override
    public OperationType<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = (JsonObject) json;

        String name = Optional.ofNullable(jsonObject.getAsJsonPrimitive(NAME))
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new JsonParseException("No required field " + NAME));

        String className = Optional.ofNullable(jsonObject.getAsJsonPrimitive(TYPE))
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new JsonParseException("No required field " + TYPE));

        //noinspection unchecked
        Class<? extends Serializable> type = (Class<? extends Serializable>) classResolver.findClass(className);
        if (type == null) {
            throw new JsonParseException("Class not found " + className);
        }

        JsonArray sigArray = jsonObject.getAsJsonArray(SIGNATURE);

        Class<?>[] signature = new Class<?>[sigArray.size()];
        for (int i = 0; i < signature.length; i++) {
            String sigClassName = sigArray.get(i).getAsString();
            Class<?> sigClass = classResolver.findClass(sigClassName);
            if (sigClass == null) {
                throw new JsonParseException("Class not found: " + sigClassName);
            }
            signature[i] = sigClass;
        }

        return new OperationType<>(name, signature, type);
    }
}
