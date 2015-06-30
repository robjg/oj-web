package org.oddjob.rest.actions;

import org.apache.log4j.Logger;
import org.oddjob.FailedToStopException;
import org.oddjob.Stoppable;

public class Stop extends SimpleAction {

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
	public void actOn(final Object node) {
		if (node instanceof Stoppable) {
			getExecutor().execute(
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

