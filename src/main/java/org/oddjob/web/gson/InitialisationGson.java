package org.oddjob.web.gson;

import com.google.gson.*;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.remote.Initialisation;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Gson serializer and deserializer for {@link Initialisation}s.
 */
public class InitialisationGson implements JsonSerializer<Initialisation<?>>, JsonDeserializer<Initialisation<?>> {

    public static final String TYPE = "type";
    public static final String DATA = "data";

    private final ClassResolver classResolver;

    public InitialisationGson(ClassResolver classResolver) {
        this.classResolver = classResolver;
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

        String className = jsonObject.getAsJsonPrimitive(TYPE).getAsString();
        //noinspection unchecked
        Class<? extends Serializable> type = (Class<? extends Serializable>) classResolver.findClass(className);
        if (type == null) {
            throw new JsonParseException("Class not found " + className);
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
