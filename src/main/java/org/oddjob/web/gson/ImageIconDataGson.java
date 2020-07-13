package org.oddjob.web.gson;

import com.google.gson.*;
import org.oddjob.images.ImageIconData;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

/**
 * Json Adapter for {@link ImageIconData}.
 */
public class ImageIconDataGson implements JsonSerializer<ImageIconData>, JsonDeserializer<ImageIconData> {

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String PIXELS = "pixels";
    public static final String DESCRIPTION = "description";

    @Override
    public JsonElement serialize(ImageIconData src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(WIDTH, src.getWidth());
        jsonObject.addProperty(HEIGHT, src.getHeight());

        ByteBuffer buffer = ByteBuffer.allocate(src.getPixels().length * 4);
        Arrays.stream(src.getPixels()).forEach(buffer::putInt);

        String pixels = Base64.getEncoder().encodeToString(buffer.array());

        jsonObject.addProperty(PIXELS, pixels);

        String description = src.getDescription();
        if (description != null) {
            jsonObject.addProperty(DESCRIPTION, description);
        }

        return jsonObject;
    }

    @Override
    public ImageIconData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        int width = jsonObject.getAsJsonPrimitive(WIDTH).getAsInt();
        int height = jsonObject.getAsJsonPrimitive(HEIGHT).getAsInt();

        String encoded = jsonObject.getAsJsonPrimitive(PIXELS).getAsString();
        byte[] bytes = Base64.getDecoder().decode(encoded);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        int[] pixels = new int[bytes.length/4];
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = buffer.getInt();
        }

        String description = Optional.ofNullable(jsonObject.getAsJsonPrimitive(DESCRIPTION))
                .map(JsonPrimitive::getAsString)
                .orElse(null);

        return new ImageIconData(width, height, pixels, description);
    }
}
