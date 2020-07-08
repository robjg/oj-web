package org.oddjob.web.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.oddjob.arooa.utils.ClassUtils;

import java.io.IOException;

/**
 * Gson Adapter for Class fields.
 */
public class ClassTypeAdapter extends TypeAdapter<Class<?>> {

    private final ClassLoader classLoader;

    public ClassTypeAdapter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void write(JsonWriter out, Class<?> value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public Class<?> read(JsonReader in) throws IOException {
        try {
            return ClassUtils.classFor(in.nextString(), classLoader);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
