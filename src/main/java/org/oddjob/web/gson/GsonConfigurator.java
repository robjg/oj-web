package org.oddjob.web.gson;

import com.google.gson.GsonBuilder;

/**
 * Configure GSON.
 */
public interface GsonConfigurator {

    GsonBuilder configure(GsonBuilder gsonBuilder);
}
