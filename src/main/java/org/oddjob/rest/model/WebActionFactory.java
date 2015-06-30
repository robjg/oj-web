package org.oddjob.rest.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.oddjob.rest.actions.Execute;
import org.oddjob.rest.actions.Force;
import org.oddjob.rest.actions.HardReset;
import org.oddjob.rest.actions.SoftReset;
import org.oddjob.rest.actions.Start;
import org.oddjob.rest.actions.Stop;

public class WebActionFactory {
	
	private final Map<String, WebAction<?>> actions = 
			new LinkedHashMap<>();
	
	public WebActionFactory(Executor executor) {
		
		// this will happen in configuration one day
		
		Start run = new Start();
		run.setExecutor(executor);
		Stop stop = new Stop();
		stop.setExecutor(executor);
		SoftReset softReset = new SoftReset();
		softReset.setExecutor(executor);
		HardReset hardReset = new HardReset();
		hardReset.setExecutor(executor);
		Force force = new Force();
		force.setExecutor(executor);
		Execute runWith = new Execute();
		runWith.setExecutor(executor);
		
		actions.put(run.getName().toLowerCase(), run);
		actions.put(stop.getName().toLowerCase(), stop);
		actions.put(softReset.getName().toLowerCase(), softReset);
		actions.put(hardReset.getName().toLowerCase(), hardReset);
		actions.put(force.getName().toLowerCase(), force);
		actions.put(runWith.getName().toLowerCase(), runWith);
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
	
	public ActionStatus performAction(Object node, String actionName,
			Object params) {
		
		WebAction<?> action = actions.get(actionName.toLowerCase());
		
		if (action == null) {
			throw new IllegalArgumentException("No Action [" + actionName + "]");
		}
		
		performWithInferredType(node, action, params);
		return ActionStatus.ok();
	}
	
	public WebDialog dialogFor(Object node, String actionName) {
		
		WebAction<?> action = actions.get(actionName.toLowerCase());
		
		if (action == null) {
			throw new IllegalArgumentException("No Action [" + actionName + "]");
		}
		
		return action.dialogFor(node);
	}
	
	protected <T> void performWithInferredType(Object node, 
			WebAction<T> action, Object params) {
		action.actOn(node, action.castParams(params));
	}
}
