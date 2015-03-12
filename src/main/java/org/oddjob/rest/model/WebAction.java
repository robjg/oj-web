package org.oddjob.rest.model;

import java.util.concurrent.Executor;

public interface WebAction {

	String getName();
	
	String getDisplayName();
	
	void actOn(Object node, Executor executor);
	
	boolean isFor(Object node);
}
