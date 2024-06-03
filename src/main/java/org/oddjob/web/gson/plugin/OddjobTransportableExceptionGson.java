package org.oddjob.web.gson.plugin;

import com.google.gson.*;
import org.oddjob.jmx.handlers.OddjobTransportableException;
import org.oddjob.web.gson.GsonConfigurator;

import java.lang.reflect.Type;

/**
 * Json Adapter for an {@link OddjobTransportableException}.
 */
public class OddjobTransportableExceptionGson implements JsonSerializer<OddjobTransportableException>, JsonDeserializer<OddjobTransportableException>, GsonConfigurator {

    @Override
    public GsonBuilder configure(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(OddjobTransportableException.class, this);
    }

    @Override
    public OddjobTransportableException deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        OddjobTransportableException.AsBean asBean =
                jsonDeserializationContext.deserialize(jsonElement, OddjobTransportableException.AsBean.class);

        return OddjobTransportableException.fromBean(asBean);
    }

    @Override
    public JsonElement serialize(OddjobTransportableException exception, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(exception.toBean());
    }
}
