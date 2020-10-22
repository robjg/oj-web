/*
 * (c) Rob Gordon 2005 - 2011.
 */
package org.oddjob.rest.model;

import org.oddjob.Iconic;
import org.oddjob.images.ImageData;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry of icons so they can be served up to 
 * the view. This registry works on the principle that
 * an icon id identifies the same icon for all jobs. 
 * 
 * @author Rob Gordon.
 */
public class IconRegistry {

	/** The icons. */
	final private Map<String, ImageData> icons = new ConcurrentHashMap<>();

	/**
	 * Register an iconId. If the icon id isn't
	 * registered already, the icon is looked up.
	 * 
	 * @param iconId The iconId.
	 * @param iconic The Iconic that can provide 
	 * the lookup.
	 */
	public void register(String iconId, Iconic iconic) {
		icons.computeIfAbsent(iconId, iconic::iconForId);
	}

	/**
	 * Retrieve an IconTip for a given icon id.
	 * 
	 * @param iconId The iconId.
	 * @return The IconTip, null if none exists if
	 * nothing is registered for that id.
	 */
	public byte[] retrieve(String iconId) {
		return Optional.ofNullable(icons.get(iconId))
				.map(ImageData::getBytes)
				.orElse(null);
	}
}
