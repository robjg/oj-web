package org.oddjob.web.gson.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.oddjob.arooa.parsing.SerializableDesignFactory;
import org.oddjob.web.gson.GsonConfigurator;

import java.io.IOException;

public class SerializableDesignFactoryGson implements TypeAdapterFactory, GsonConfigurator {

    @Override
    public GsonBuilder configure(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapterFactory(this);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!SerializableDesignFactory.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                out.beginObject();
                out.endObject();
            }

            @Override
            public T read(JsonReader in) throws IOException {
                in.beginObject();
                in.endObject();
                return null;
            }
        };
    }
}
