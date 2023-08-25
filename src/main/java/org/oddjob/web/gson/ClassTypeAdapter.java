package org.oddjob.web.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.oddjob.arooa.ClassResolver;

import java.io.IOException;

/**
 * Gson Adapter for Class fields.
 */
public class ClassTypeAdapter extends TypeAdapter<Class<?>> {

    private final ClassResolver classResolver;

    public ClassTypeAdapter(ClassResolver classResolver) {
        this.classResolver = classResolver;
    }

    @Override
    public void write(JsonWriter out, Class<?> value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public Class<?> read(JsonReader in) throws IOException {
        String className = in.nextString();
        Class<?> cl = classResolver.findClass(className);
        if (cl == null) {
            throw new JsonParseException("Class not found: " + className);
        }
        return cl;
    }
}
