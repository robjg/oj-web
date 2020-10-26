package org.oddjob.web.gson;

import com.google.gson.*;
import org.oddjob.remote.Implementation;
import org.oddjob.remote.Initialisation;

import java.lang.reflect.Type;

/**
 * Gson deserializer for {@link Implementation}s.
 */
public class ImplementationGson implements JsonSerializer<Implementation<?>>, JsonDeserializer<Implementation<?>> {

    public static final String TYPE = "type";
    public static final String VERSION = "version";
    public static final String INITIALISATION = "initialisation";

    @Override
    public JsonElement serialize(Implementation<?> src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(TYPE, src.getType());
        jsonObject.addProperty(VERSION, src.getVersion());

        if (src.getInitialisation() != null) {
            jsonObject.add(INITIALISATION, context.serialize(src.getInitialisation()));
        }

        return jsonObject;
    }

    @Override
    public Implementation<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        String type = jsonObject.getAsJsonPrimitive(TYPE).getAsString();
        String version = jsonObject.getAsJsonPrimitive(VERSION).getAsString();

        JsonElement initialisationJson = jsonObject.get(INITIALISATION);

        if (initialisationJson == null) {
            return Implementation.create(type, version);
        }
        else {
            return Implementation.create(type, version,
                    context.deserialize(initialisationJson, Initialisation.class));
        }
    }

}
