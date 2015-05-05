package org.oddjob.rest.actions;

import org.apache.log4j.Logger;
import org.oddjob.Resetable;

public class SoftReset extends NoParamsAction {

	private static final Logger logger = Logger.getLogger(SoftReset.class);
	
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

