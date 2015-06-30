package org.oddjob.rest.actions;

import java.util.Properties;

import org.oddjob.input.InputRequest;
import org.oddjob.jobs.tasks.BasicTask;
import org.oddjob.jobs.tasks.TaskException;
import org.oddjob.jobs.tasks.TaskExecutor;
import org.oddjob.rest.model.WebForm;

public class Execute extends FormAction {

	private final String name = "execute";
	
	private final String displayName = "Execute";

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
		return node instanceof TaskExecutor;
	}
	
	@Override
	public WebForm dialogFor(Object node) {
		InputRequest[] requests = ((TaskExecutor) node).getParameterInfo();
		
		if (requests == null) {
			return null;
		}
		
		return WebForm.createFrom(requests);
	}
	
	@Override
	public void actOn(final Object node, final Properties properties) {
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					((TaskExecutor) node).execute(new BasicTask(properties));
				} catch (TaskException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}	
}
