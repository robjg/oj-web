package org.oddjob.web.gson.plugin;

import com.google.gson.*;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.web.gson.GsonConfigurator;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * Json Adapter for a Hierarchical Array Bean Type.
 */
public class HierarchicalArrayGson<T> implements JsonSerializer<T[]>, JsonDeserializer<T[]>, GsonConfigurator {

    public static final String CLASS_NAME = "className";

    public static final String ELEMENT = "element";

    private final Class<T[]> arrayType;

    private final ClassResolver classResolver;

    private HierarchicalArrayGson(Class<T[]> arrayType, ClassResolver classResolver) {
        this.arrayType = arrayType;
        this.classResolver = classResolver;
    }

    public static <T> GsonConfigurator forHierarchicalArray(Class<T[]> arrayType, ClassResolver classResolver) {

        return new HierarchicalArrayGson<>(arrayType, classResolver);
    }

    @Override
    public GsonBuilder configure(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(arrayType, this);
    }


    @Override
    public JsonElement serialize(T[] src, Type typeOfSrc, JsonSerializationContext context) {

        JsonArray jsonArray = new JsonArray();

        for (T t : src) {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty(CLASS_NAME, t.getClass().getName());

            JsonElement object = context.serialize(t, t.getClass());

            jsonObject.add(ELEMENT, object);

            jsonArray.add(jsonObject);

        }

        return jsonArray;
    }

    @Override
    public T[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray jsonArray = json.getAsJsonArray();

        T[] ta = (T[]) Array.newInstance(arrayType.getComponentType(), jsonArray.size());

        int i = 0;
        for (JsonElement element : jsonArray) {

            JsonObject jsonObject = element.getAsJsonObject();

            String className = jsonObject.getAsJsonPrimitive(CLASS_NAME).getAsString();

            //noinspection unchecked
            Class<T> cl = (Class<T>) classResolver.findClass(className);

            if (cl == null) {
                throw new JsonParseException("No class found: " + className);
            }

            T t = context.deserialize(jsonObject.get(ELEMENT), cl);

            ta[i++] = t;
        }

        return ta;
    }

}
