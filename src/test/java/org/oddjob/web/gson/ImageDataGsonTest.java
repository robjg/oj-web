package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.oddjob.images.IconHelper;
import org.oddjob.images.ImageData;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ImageDataGsonTest {


    @Test
    public void foo() throws IOException {

        URL url = IconHelper.class.getResource("triangle_green.gif");

        ImageData imageData = ImageData.fromUrl(url, "Executing");

        System.out.println(Base64.getEncoder().encodeToString(imageData.getBytes()));
    }


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

}