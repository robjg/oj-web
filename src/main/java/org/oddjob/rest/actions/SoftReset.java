package org.oddjob.rest.actions;

import org.oddjob.Resettable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoftReset extends SimpleAction {

	private static final Logger logger = LoggerFactory.getLogger(SoftReset.class);
	
	private static final String NAME = "soft-reset";
	
	private static final String DISPLAY_NAME = "Soft Reset";

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
							((Resettable) node).softReset();
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

