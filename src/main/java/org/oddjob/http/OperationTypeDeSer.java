package org.oddjob.http;

import com.google.gson.*;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.remote.OperationType;

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

    private final ClassLoader classLoader;

    public OperationTypeDeSer(ClassLoader classLoader) {
        this.classLoader = classLoader;
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

        Class<?> type;
        try {
            type = ClassUtils.classFor(
                    Optional.ofNullable(jsonObject.getAsJsonPrimitive(TYPE))
                            .map(JsonPrimitive::getAsString)
                            .orElseThrow(() -> new JsonParseException("No required field " + TYPE)),
                    classLoader);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }

        JsonArray sigArray = jsonObject.getAsJsonArray(SIGNATURE);

        Class<?>[] signature = new Class<?>[sigArray.size()];
        for (int i = 0; i <signature.length; i++) {
            try {
                signature[i] = ClassUtils.classFor(sigArray.get(i).getAsString(), classLoader);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        return new OperationType<>(name, signature, type);
    }
}
