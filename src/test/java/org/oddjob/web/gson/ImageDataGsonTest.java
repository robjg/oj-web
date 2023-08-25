package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.oddjob.images.IconHelper;
import org.oddjob.images.ImageData;
import org.oddjob.web.gson.plugin.ImageDataGson;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ImageDataGsonTest {

    @Test
    public void testSerializeDeserialize() {

        ImageData test = IconHelper.executingIcon;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ImageData.class, new ImageDataGson())
                .create();

        String json = gson.toJson(test);

        System.out.println(json);

        ImageData copy = gson.fromJson(json, ImageData.class);

        assertThat(copy.getBytes(), is(test.getBytes()));
        assertThat(copy.getMimeType(), is(test.getMimeType()));
        assertThat(copy.getDescription(), is(test.getDescription()));
    }

    // Gets base64 for web.
    public static void main(String[] args) throws IOException {

        URL url = Objects.requireNonNull(ImageDataGsonTest.class.getResource(
                "/org/oddjob/webapp/gfx/plus.png"));

        ImageData imageData = ImageData.fromUrl(url, null);

        System.out.println(imageData.getMimeType());
        System.out.println(Base64.getEncoder().encodeToString(
                imageData.getBytes()));
    }
}