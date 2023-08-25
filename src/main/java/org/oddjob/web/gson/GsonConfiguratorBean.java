package org.oddjob.web.gson;

import com.google.gson.GsonBuilder;

/**
 * A {@link GsonConfiguratorBean} to be used in a {@code oj-gson} configuration file.
 *
 * @author rob
 *
 */
public class GsonConfiguratorBean implements GsonConfigurator {

	/** The Gson Configurators property */
	private GsonConfigurator[] configurators;
	
	/**
	 * Setter.
	 * 
	 * @param configurators The Configurators.
	 */
	public void setConfigurators(GsonConfigurator[] configurators) {
		this.configurators = configurators;
	}

	@Override
	public GsonBuilder configure(GsonBuilder gsonBuilder) {

		if (configurators != null) {
			for (GsonConfigurator configurator : configurators) {

				configurator.configure(gsonBuilder);
			}
		}

		return gsonBuilder;
	}
}
