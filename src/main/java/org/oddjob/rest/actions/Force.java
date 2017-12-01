package org.oddjob.rest.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.Forceable;

public class Force extends SimpleAction {

	private static final Logger logger = LoggerFactory.getLogger(Force.class);

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
	public void actOn(final Object node) {
		if (node instanceof Forceable) {
			getExecutor().execute(
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
