package org.oddjob.rest.model;


public interface WebAction<T> {

	String getName();
	
	String getDisplayName();
	
	public Class<?> getParamsType();
	
	public T castParams(Object params);
	
	void actOn(Object node, T params);
	
	boolean isFor(Object node);
}
