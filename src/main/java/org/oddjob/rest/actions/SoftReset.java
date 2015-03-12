package org.oddjob.rest.actions;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.oddjob.Resetable;
import org.oddjob.rest.model.WebAction;

public class SoftReset implements WebAction {

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
	public void actOn(final Object node, Executor executor) {
		if (node instanceof Resetable) {
			executor.execute(
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

