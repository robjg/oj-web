package org.oddjob.web.gson.plugin;

import com.google.gson.*;
import org.oddjob.state.StateInstant;
import org.oddjob.web.gson.GsonConfigurator;

import java.lang.reflect.Type;

/**
 * Json Adapter for an {@link StateInstant}.
 */
public class StateInstantGson implements JsonSerializer<StateInstant>, JsonDeserializer<StateInstant>, GsonConfigurator {

    @Override
    public GsonBuilder configure(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(StateInstant.class, this);
    }

    @Override
    public StateInstant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return StateInstant.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(StateInstant instant, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(instant.getInstant().toString());
    }
}
