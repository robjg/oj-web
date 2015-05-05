package org.oddjob.rest.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.oddjob.rest.actions.Force;
import org.oddjob.rest.actions.HardReset;
import org.oddjob.rest.actions.Run;
import org.oddjob.rest.actions.SoftReset;
import org.oddjob.rest.actions.Stop;

public class WebActionFactory {
	
	private static final Logger logger = Logger.getLogger(WebActionFactory.class);
	
	private final Map<String, WebAction<?>> actions = 
			new LinkedHashMap<>();
	
	public WebActionFactory(Executor executor) {
		
		// this will happen in configuration one day
		
		Run run = new Run();
		run.setExecutor(executor);
		Stop stop = new Stop();
		stop.setExecutor(executor);
		SoftReset softReset = new SoftReset();
		softReset.setExecutor(executor);
		HardReset hardReset = new HardReset();
		hardReset.setExecutor(executor);
		Force force = new Force();
		force.setExecutor(executor);
		
		actions.put(run.getName(), run);
		actions.put(stop.getName(), stop);
		actions.put(softReset.getName(), softReset);
		actions.put(hardReset.getName(), hardReset);
		actions.put(force.getName(), force);
	}
	
	public WebAction<?>[] actionsFor(Object node) {

		List<WebAction<?>> results = new ArrayList<>();
		
		for (WebAction<?> action : actions.values()) {
			if (action.isFor(node)) {
				results.add(action);
			};
		}
		
		return results.toArray(new WebAction[results.size()]);		
	}
	
	public void performAction(Object node, String actionName,
			Object params) {
		
		WebAction<?> action = actions.get(actionName.toLowerCase());
		
		if (action == null) {
			logger.info("No Action [" + action + "]");
		}
		else {
			performWithInferredType(node, action, params);
		}
	}
	
	protected <T> void performWithInferredType(Object node, 
			WebAction<T> action, Object params) {
		action.actOn(node, action.castParams(params));
	}
}
