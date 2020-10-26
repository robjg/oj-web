package org.oddjob.web.gson;

import com.google.gson.*;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.remote.Initialisation;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Gson serializer and deserializer for {@link Initialisation}s.
 */
public class InitialisationGson implements JsonSerializer<Initialisation<?>>, JsonDeserializer<Initialisation<?>> {

    public static final String TYPE = "type";
    public static final String DATA = "data";

    private final ClassLoader classLoader;

    public InitialisationGson(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public JsonElement serialize(Initialisation<?> src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, src.getType().getName());

        if (src.getType() != void.class && src.getType() != Void.class) {
            jsonObject.add(DATA, context.serialize(src.getData(), src.getType()));
        }

        return jsonObject;
    }

    @Override
    public Initialisation<? extends Serializable> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = (JsonObject) json;

        Class<? extends Serializable> type;
        try {
            type = (Class<? extends Serializable>) ClassUtils.classFor(
                    jsonObject.getAsJsonPrimitive(TYPE).getAsString(), classLoader);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }

        JsonElement valueJson = jsonObject.get(DATA);

        if (valueJson == null) {
            return Initialisation.from(type, null );
        }
        else {
            return Initialisation.from(type, context.deserialize(valueJson, type));
        }
    }
}
