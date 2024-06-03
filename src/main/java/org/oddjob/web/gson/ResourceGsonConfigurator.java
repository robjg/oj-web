package org.oddjob.web.gson;

import com.google.gson.GsonBuilder;
import org.oddjob.arooa.ArooaSession;

import java.net.URL;
import java.util.Objects;

/**
 * An {@link GsonConfigurator} that provides Configurators for GSON from
 * any number of XML Configurations found on the class path.
 * 
 * @author rob
 *
 */
public class ResourceGsonConfigurator implements GsonConfigurator {

	/** The resource name. */
	public static final String DEFAULT_RESOURCE_NAME = "META-INF/oj-gson.xml";

	/** The session to use for finding resources and parsing
	 * the configurations. */
	private final ArooaSession session;

	private final String resourceName;

	/**
	 * Constructor.
	 *
	 * @param session The Arooa Session.
	 */
	public ResourceGsonConfigurator(ArooaSession session) {
		this(session, DEFAULT_RESOURCE_NAME);
	}

	public ResourceGsonConfigurator(ArooaSession session, String resourceName) {
		this.session = Objects.requireNonNull(session);
		this.resourceName = Objects.requireNonNull(resourceName);
	}

	@Override
	public GsonBuilder configure(GsonBuilder gsonBuilder) {

		URL[] urls = session.getArooaDescriptor().getClassResolver().getResources(
				resourceName);
		
		return new URLGsonConfigurator(urls, session).configure(gsonBuilder);
	}

}
