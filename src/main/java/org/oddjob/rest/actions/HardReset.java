package org.oddjob.rest.actions;

import org.apache.log4j.Logger;
import org.oddjob.Resetable;

public class HardReset extends SimpleAction {

	private static final Logger logger = Logger.getLogger(HardReset.class);

	private final String name = "hard-reset";
	
	private final String displayName = "Hard Reset";

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
							((Resetable) node).hardReset();
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
