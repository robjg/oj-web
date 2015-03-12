package org.oddjob.rest.actions;

import java.util.concurrent.Executor;

import org.oddjob.rest.model.WebAction;

public class Run implements WebAction {

	private final String name = "run";
	
	private final String displayName = "Run";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void actOn(Object node, Executor executor) {
		if (node instanceof Runnable) {
			executor.execute((Runnable) node);
		}
	}

	@Override
	public boolean isFor(Object node) {
		return node instanceof Runnable;
	}

}
