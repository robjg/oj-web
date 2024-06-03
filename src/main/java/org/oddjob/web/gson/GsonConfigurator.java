package org.oddjob.web.gson;

import com.google.gson.GsonBuilder;

/**
 * Configure GSON.
 */
public interface GsonConfigurator {

    /**
     * Configure a GsonBuilder by adding Type Adapters etc. to it.
     *
     * @param gsonBuilder The Builder
     *
     * @return The same Builder for fluidity.
     */
    GsonBuilder configure(GsonBuilder gsonBuilder);
}
