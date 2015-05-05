package org.oddjob.rest.actions;

import java.util.concurrent.Executor;

public class BaseAction {

	private transient volatile Executor executor;
	
	public Executor getExecutor() {
		return executor;
	}
	
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}
