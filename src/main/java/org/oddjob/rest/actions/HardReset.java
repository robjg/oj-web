package org.oddjob.rest.actions;

import org.oddjob.Resettable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardReset extends SimpleAction {

	private static final Logger logger = LoggerFactory.getLogger(HardReset.class);

	private static final String NAME = "hard-reset";
	
	private static final String DISPLAY_NAME = "Hard Reset";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public void actOn(final Object node) {
		if (node instanceof Resettable) {
			getExecutor().execute(
					() -> {
						try {
							((Resettable) node).hardReset();
						} catch (RuntimeException e) {
							logger.info("Failed to Hard Reset [" + node + "]",
									e);
						}
					});
		}
	}

	@Override
	public boolean isFor(Object node) {
		return node instanceof Resettable;
	}

}
