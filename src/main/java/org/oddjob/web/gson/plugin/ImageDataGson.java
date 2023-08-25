package org.oddjob.web.gson.plugin;

import com.google.gson.*;
import org.oddjob.images.ImageData;
import org.oddjob.web.gson.GsonConfigurator;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Optional;

/**
 * Json Adapter for {@link ImageData}.
 */
public class ImageDataGson implements JsonSerializer<ImageData>, JsonDeserializer<ImageData>, GsonConfigurator {

    public static final String BYTES = "bytes";
    public static final String MIME_TYPE = "mimeType";
    public static final String DESCRIPTION = "description";

    @Override
    public GsonBuilder configure(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(ImageData.class, this);
    }

    @Override
    public JsonElement serialize(ImageData src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(MIME_TYPE, src.getMimeType());

        String bytes = Base64.getEncoder().encodeToString(src.getBytes());

        jsonObject.addProperty(BYTES, bytes);

        String description = src.getDescription();
        if (description != null) {
            jsonObject.addProperty(DESCRIPTION, description);
        }

        return jsonObject;
    }

    @Override
    public ImageData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        String mimeType = jsonObject.getAsJsonPrimitive(MIME_TYPE).getAsString();

        String encoded = jsonObject.getAsJsonPrimitive(BYTES).getAsString();
        byte[] bytes = Base64.getDecoder().decode(encoded);

        String description = Optional.ofNullable(jsonObject.getAsJsonPrimitive(DESCRIPTION))
                .map(JsonPrimitive::getAsString)
                .orElse(null);

        return new ImageData(bytes, mimeType, description);
    }
}
