package org.oddjob.rest.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.Resetable;

public class SoftReset extends SimpleAction {

	private static final Logger logger = LoggerFactory.getLogger(SoftReset.class);
	
	private final String name = "soft-reset";
	
	private final String displayName = "Soft Reset";

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
		if (node instanceof Resetable) {
			getExecutor().execute(
				new Runnable() {
					@Override
					public void run() {
						try {
							((Resetable) node).softReset();
						} catch (RuntimeException e) {
							logger.info("Failed to Hard Reset [" + node + "]", 
									e);
						}
					}
				});
		}
	}

	@Override
	public boolean isFor(Object node) {
		return node instanceof Resetable;
	}

}

