package org.oddjob.rest.actions;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.oddjob.Forceable;
import org.oddjob.Resetable;
import org.oddjob.rest.model.WebAction;

public class Force implements WebAction {

	private static final Logger logger = Logger.getLogger(Force.class);

	private final String name = "force";
	
	private final String displayName = "Force";

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
		if (node instanceof Forceable) {
			executor.execute(
				new Runnable() {
					@Override
					public void run() {
						try {
							((Forceable) node).force();
						} catch (RuntimeException e) {
							logger.info("Failed to Force [" + node + "]", 
									e);
						}
					}
				});
		}
	}

	@Override
	public boolean isFor(Object node) {
		return node instanceof Forceable;
	}

}
