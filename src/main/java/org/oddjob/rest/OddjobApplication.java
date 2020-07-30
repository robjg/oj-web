package org.oddjob.rest;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.describe.UniversalDescriber;
import org.oddjob.jmx.RemoteIdMappings;
import org.oddjob.rest.model.OddjobTrackerLocal;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Main entry point into the Web Service. See 
 * <a href="https://jax-rs-spec.java.net/nonav/2.0-rev-a/apidocs/javax/ws/rs/core/Application.html">Application</>. 
 * 
 * 
 * @author rob
 *
 */
public class OddjobApplication extends Application {
	
	public static final String ROOT_ATTRIBUTE_NAME = "oj-root-node";

	public static final String UPLOAD_DIR_ATTRIBUTE_NAME = "oj-upload-dir";

	public static final String ID_MAPPINGS_ATTRIBUTE_NAME = "oj-id-mappings";

	public static final String SESSION_ATTRIBUTE_NAME = "oj-session";

	private Set<Object> services;
	
	/** The standard Servlet Context. Injected by the Web Service Framework. */
	@Context
	private ServletContext servletContext;
	
	@Override
	public synchronized Set<Object> getSingletons() {
		
		if (services == null) {
			services = new HashSet<>();

			ArooaSession session = (ArooaSession) Objects.requireNonNull(
					servletContext.getAttribute(SESSION_ATTRIBUTE_NAME),
							"No " + SESSION_ATTRIBUTE_NAME + " in Servlet Context.");

			File uploadDirectory = (File) Objects.requireNonNull(
					servletContext.getAttribute(UPLOAD_DIR_ATTRIBUTE_NAME),
					"No " + UPLOAD_DIR_ATTRIBUTE_NAME + " in Servlet Context.");

			OddjobTrackerLocal oddjobTracker;

			RemoteIdMappings idMappings = (RemoteIdMappings) servletContext.getAttribute(ID_MAPPINGS_ATTRIBUTE_NAME);
			if (idMappings == null) {

				Object root = servletContext.getAttribute(ROOT_ATTRIBUTE_NAME);
				if (root == null) {
					throw new NullPointerException("No " + ROOT_ATTRIBUTE_NAME +
							" or " + ID_MAPPINGS_ATTRIBUTE_NAME +
							" in Servlet Context.");
				}

				oddjobTracker = new OddjobTrackerLocal(
						session.getBeanRegistry(), new UniversalDescriber(session));
				oddjobTracker.track(root);
			}
			else {
				oddjobTracker = new OddjobTrackerLocal(
						idMappings,
						session.getBeanRegistry(), new UniversalDescriber(session));
				oddjobTracker.track(idMappings.objectFor(1L));
			}

			services.add(new OddjobApiImpl(oddjobTracker, uploadDirectory));
			
			return services;
		}
		else {
			return services;
		}
	}

}
