package org.oddjob.rest.actions;

import java.util.Properties;

import org.oddjob.jobs.ParameterisedExecution;

public class RunWith extends PropertiesAction {

	private final String name = "runWith";
	
	private final String displayName = "Run With";

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public boolean isFor(Object node) {
		return node instanceof ParameterisedExecution;
	}
	
	@Override
	public void actOn(final Object node, final Properties properties) {
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				((ParameterisedExecution) node).runWith(properties);
			}
		});
	}	
}
