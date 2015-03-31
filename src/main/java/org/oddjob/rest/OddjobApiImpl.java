package org.oddjob.rest;

import java.util.concurrent.Executors;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.oddjob.rest.model.LogLines;
import org.oddjob.rest.model.NodeInfos;
import org.oddjob.rest.model.OddjobTracker;
import org.oddjob.rest.model.WebAction;
import org.oddjob.rest.model.WebActionFactory;

import com.google.gson.Gson;

public class OddjobApiImpl implements OddjobApi {

	private static final Logger logger = Logger.getLogger(OddjobApiImpl.class);
	
	private final OddjobTracker tracker;
	
	private final WebActionFactory actionFactory =
			new WebActionFactory(Executors.newFixedThreadPool(2));
	
	public OddjobApiImpl(Object rootNode) {
		tracker = new OddjobTracker();
		tracker.track(rootNode);
	}

	@Override
	public Response nodeInfo(String nodeIds, long eventSeq) {

		String[] nodeIdsStrArray = nodeIds.split(",");
		int[] nodeIdsArray = new int[nodeIdsStrArray.length];
		for (int i = 0; i < nodeIdsArray.length; ++i) {
			nodeIdsArray[i] = Integer.parseInt(nodeIdsStrArray[i]);
		}
		NodeInfos nodeInfos = tracker.infoFor(eventSeq, nodeIdsArray);
		Gson gson = new Gson();
		String json = gson.toJson(nodeInfos);  
	
		if (logger.isDebugEnabled()) {
			logger.debug("nodeInfo(" + nodeIds + ", " + eventSeq + 
					"), Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
	}
	
	
	@Override
	public Response iconFor(String iconId) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Icon Request [" + iconId + "]");
		}
		
		return Response.ok(tracker.iconImageFor(iconId)).build();
	}
	
	@Override
	public Response actionsFor(String nodeId) {
		
		Object node = tracker.nodeFor(Integer.parseInt(nodeId));
		
		WebAction[] actions = actionFactory.actionsFor(node);
		
		Gson gson = new Gson();
		String json = gson.toJson(actions);  
	
		if (logger.isDebugEnabled()) {
			logger.debug("actionsFor(" + nodeId+ 
					"), Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
	}
	
	@Override
	public void performAction(String nodeId, String actionName) {
		
		int nodeIdInt;
		try {
			nodeIdInt = Integer.parseInt(nodeId);
		}
		catch (NumberFormatException e) {
			logger.error("Failed parsing Node Id [" + nodeId + "]: " +
					e.toString());
			return;
		}
		
		Object node = tracker.nodeFor(nodeIdInt);
		
		if (node == null) {
			logger.error("No Node for Id [" + nodeId + "]: ");
			return;
		}
		
		actionFactory.performAction(node, actionName);
	}
	
	@Override
	public Response logLines(String nodeId, String logSeq) {
		
		LogLines logLines = tracker.logLinesFor(
				Integer.parseInt(nodeId),
				Long.parseLong(logSeq));
		
		Gson gson = new Gson();
		String json = gson.toJson(logLines);  
	
		if (logger.isDebugEnabled()) {
			logger.debug("logLines(" + nodeId+ ", " + logSeq +
					"), Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
	}
}
