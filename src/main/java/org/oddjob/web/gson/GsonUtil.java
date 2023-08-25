package org.oddjob.web.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.http.*;
import org.oddjob.remote.*;
import org.oddjob.websocket.NotificationDeserializer;
import org.oddjob.websocket.NotificationTypeDesSer;

/**
 * Group all Gson Adapters.
 */
public class GsonUtil {

    public static Gson createGson(ArooaSession arooaSession) {

        ClassResolver classResolver = arooaSession.getArooaDescriptor().getClassResolver();

        return createGson(new ResourceGsonConfigurator(arooaSession),
                classResolver);
    }

    public static Gson createGson(GsonConfigurator gsonConfigurator,
                                  ClassResolver classResolver) {

        return gsonConfigurator.configure(
                new DefaultConfigurator(classResolver)
                .configure(new GsonBuilder()))
                .create();
    }

    public static Gson defaultGson() {
        return defaultGson(new GsonBuilder(), ClassResolver.getDefaultClassResolver());
    }

    public static Gson defaultGson(GsonBuilder gsonBuilder, ClassResolver classLoader) {
        return new DefaultConfigurator(classLoader).configure(gsonBuilder)
                .create();
    }

    public static class DefaultConfigurator implements GsonConfigurator {

        private final ClassResolver classResolver;

        public DefaultConfigurator(ClassResolver classResolver) {
            this.classResolver = classResolver;
        }


        @Override
        public GsonBuilder configure(GsonBuilder gsonBuilder) {

                return gsonBuilder.registerTypeAdapter(Class.class, new ClassTypeAdapter(classResolver))

                    .registerTypeAdapter(OperationType.class,
                            new OperationTypeDeSer(classResolver))

                    .registerTypeAdapter(InvokeRequest.class,
                            new InvokeRequestGson())
                    .registerTypeAdapter(InvokeResponse.class,
                            new InvokeResponseDesSer(classResolver))

                    .registerTypeAdapter(NotificationType.class,
                            new NotificationTypeDesSer(classResolver))
                    .registerTypeAdapter(Notification.class,
                            new NotificationDeserializer())

                    .registerTypeAdapter(Initialisation.class,
                            new InitialisationGson(classResolver))
                    .registerTypeAdapter(Implementation.class,
                            new ImplementationGson());
        }
    }
}
