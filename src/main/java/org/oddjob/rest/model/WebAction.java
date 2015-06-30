package org.oddjob.rest.model;


public interface WebAction<T> {

	String getName();
	
	String getDisplayName();
	
	boolean isFor(Object node);
	
	Class<?> getParamsType();
	
	T castParams(Object params);
	
	WebForm	dialogFor(Object node);
	
	void actOn(Object node, T params);
}
