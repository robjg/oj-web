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
 * Provides a {@code Gson} by loading adapters as plugins and/or by providing
 * defaults.  which are those adapters required for the Web implementation of
 * an {@link RemoteConnection}.
 *
 * @see ResourceGsonConfigurator
 */
public class GsonUtil {

    /**
     * Create a {@code Gson} from Plugins and Defaults.
     *
     * @param arooaSession The Arooa Session for finding and parsing the plugin file.
     *
     * @return A configured Gson.
     */
    public static Gson createGson(ArooaSession arooaSession) {

        ClassResolver classResolver = arooaSession.getArooaDescriptor().getClassResolver();

        return createGson(new ResourceGsonConfigurator(arooaSession),
                classResolver);
    }

    /**
     * Create a {@code Gson} from a {@code GsonConfigurator} and Defaults.
     *
     * @param gsonConfigurator The GsonConfigurator.
     * @param classResolver The ClassResolver.
     *
     * @return A configured Gson.
     */
    public static Gson createGson(GsonConfigurator gsonConfigurator,
                                  ClassResolver classResolver) {

        return gsonConfigurator.configure(
                new DefaultConfigurator(classResolver)
                .configure(new GsonBuilder()))
                .create();
    }

    /**
     * Create a {@code Gson} just from defaults.
     *
     * @return A configured Gson.
     */
    public static Gson defaultGson() {
        return defaultGson(new GsonBuilder(), ClassResolver.getDefaultClassResolver());
    }

    /**
     * Create a {@code Gson} just from defaults using the given Gson Builder and
     * Class Resolver.
     *
     * @param gsonBuilder The Gson Builder.
     * @param classResolver The Class Resolver
     *
     * @return A configured Gson.
     */
    public static Gson defaultGson(GsonBuilder gsonBuilder, ClassResolver classResolver) {
        return new DefaultConfigurator(classResolver).configure(gsonBuilder)
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
