package org.oddjob.rest.actions;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.oddjob.FailedToStopException;
import org.oddjob.Stoppable;
import org.oddjob.rest.model.WebAction;

public class Stop implements WebAction {

	private static final Logger logger = Logger.getLogger(Stop.class);
	
	private final String name = "stop";
	
	private final String displayName = "Stop";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void actOn(final Object node, Executor executor) {
		if (node instanceof Stoppable) {
			executor.execute(
				new Runnable() {
					@Override
					public void run() {
						try {
							((Stoppable) node).stop();
						} catch (FailedToStopException e) {
							logger.info("Failed to Stop [" + node + "]", 
									e);
						}
					}
				});
		}
	}

	@Override
	public boolean isFor(Object node) {
		return node instanceof Stoppable;
	}
}

