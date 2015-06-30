package org.oddjob.rest.actions;

import org.oddjob.rest.model.WebAction;
import org.oddjob.rest.model.WebForm;

abstract public class SimpleAction extends BaseAction 
implements WebAction<Void> {

	@Override
	public Void castParams(Object params) {
		if (params != null) {
			throw new IllegalArgumentException(
				"No Parameters Expected for action [" + 
				getDisplayName() + "]");
		}
		return null;
	}
	
	@Override
	public Class<?> getParamsType() {
		return Void.class;
	}
	
	@Override
	public WebForm dialogFor(Object node) {
		return null;
	}
	
	@Override
	public final void actOn(Object node, Void params) {
		actOn(node);
	}
	
	abstract protected void actOn(Object node);
}
