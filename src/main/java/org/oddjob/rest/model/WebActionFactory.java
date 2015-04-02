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
	
	private final Executor executor;

	private final Map<String, WebAction> actions = 
			new LinkedHashMap<String, WebAction>();
	
	public WebActionFactory(Executor executor) {
		this.executor = executor;
		
		Run run = new Run();
		Stop stop = new Stop();
		SoftReset softReset = new SoftReset();
		HardReset hardReset = new HardReset();
		Force force = new Force();
		
		actions.put(run.getName(), run);
		actions.put(stop.getName(), stop);
		actions.put(softReset.getName(), softReset);
		actions.put(hardReset.getName(), hardReset);
		actions.put(force.getName(), force);
	}
	
	public WebAction[] actionsFor(Object node) {

		List<WebAction> results = new ArrayList<WebAction>();
		
		for (WebAction action : actions.values()) {
			if (action.isFor(node)) {
				results.add(action);
			};
		}
		
		return results.toArray(new WebAction[results.size()]);		
	}
	
	public void performAction(Object node, String actionName) {
		WebAction action = actions.get(actionName.toLowerCase());
		if (action == null) {
			logger.info("No Action [" + action + "]");
		}
		else {
			action.actOn(node, executor);
		}
	}
}
