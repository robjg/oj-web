package org.oddjob.rest.actions;

import java.util.Properties;

import org.oddjob.rest.model.WebAction;

abstract public class PropertiesAction extends BaseAction 
implements WebAction<Properties> {

	@Override
	public Properties castParams(Object params) {
		return (Properties) params;
	}
	
	@Override
	public Class<?> getParamsType() {
		return Properties.class;
	}
	
}
