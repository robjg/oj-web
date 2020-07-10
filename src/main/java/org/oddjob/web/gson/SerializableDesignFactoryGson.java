package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.oddjob.arooa.parsing.SerializableDesignFactory;

import java.io.IOException;

public class SerializableDesignFactoryGson implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!SerializableDesignFactory.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return new TypeAdapter<T>() {
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
