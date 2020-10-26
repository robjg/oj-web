package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oddjob.http.*;
import org.oddjob.images.ImageData;
import org.oddjob.remote.*;
import org.oddjob.websocket.NotificationDeserializer;
import org.oddjob.websocket.NotificationTypeDesSer;

/**
 * Group all Gson Adapters.
 *
 * TODO Think how to make this pluggable.
 */
public class GsonUtil {

    public static Gson createGson(ClassLoader classLoader) {

        return new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter(classLoader))

                .registerTypeAdapter(OperationType.class,
                        new OperationTypeDeSer(classLoader))

                .registerTypeAdapter(InvokeRequest.class,
                        new InvokeRequestGson())
                .registerTypeAdapter(InvokeResponse.class,
                        new InvokeResponseDesSer(classLoader))

                .registerTypeAdapter(NotificationType.class,
                        new NotificationTypeDesSer(classLoader))
                .registerTypeAdapter(Notification.class,
                        new NotificationDeserializer())

                .registerTypeAdapter(Initialisation.class,
                        new InitialisationGson(classLoader))
                .registerTypeAdapter(Implementation.class,
                        new ImplementationGson())

                .registerTypeAdapter(ImageData.class, new ImageDataGson())

                .registerTypeAdapterFactory(
                        new SerializableDesignFactoryGson())

                .create();
    }
}
