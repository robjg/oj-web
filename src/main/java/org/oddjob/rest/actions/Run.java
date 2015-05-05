package org.oddjob.rest.actions;

import org.oddjob.jobs.job.ResetActions;

public class Run extends NoParamsAction {

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
	public void actOn(final Object node) {
		if (node instanceof Runnable) {
			getExecutor().execute(new Runnable() {
				@Override
				public void run() {
					ResetActions.AUTO.doWith(node);
					((Runnable) node).run();
				};
			});
		}
	}

	@Override
	public boolean isFor(Object node) {
		return node instanceof Runnable;
	}
}
