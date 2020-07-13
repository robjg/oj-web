package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.oddjob.images.IconHelper;
import org.oddjob.images.ImageIconData;

import javax.swing.*;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ImageIconDataGsonTest {


    @Test
    public void testSerializeDeserialize() throws IOException {

        ImageIcon executing = IconHelper.executingIcon;

        ImageIconData test = ImageIconData.fromImageIcon(executing);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ImageIconData.class, new ImageIconDataGson())
                .create();

        String json = gson.toJson(test);

        System.out.println(json);

        ImageIconData copy = gson.fromJson(json, ImageIconData.class);

        assertThat(copy.getWidth(), is(test.getWidth()));
        assertThat(copy.getHeight(), is(test.getHeight()));
        assertThat(copy.getPixels(), is(test.getPixels()));
        assertThat(copy.getDescription(), is(test.getDescription()));
    }

}