package org.oddjob.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oddjob.rest.model.*;
import org.oddjob.rest.util.FormData;
import org.oddjob.rest.util.MultipartRequestMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.Executors;

/**
 * Implementation of the Oddjob Web Service API.
 * 
 * @author rob
 *
 */
public class OddjobApiImpl implements OddjobApi {

	private static final Logger logger = LoggerFactory.getLogger(OddjobApiImpl.class);

	/** Track changes in Oddjob. */
	private final OddjobTracker tracker;
	
	private final File uploadDirectory;
	
	private final WebActionFactory actionFactory =
			new WebActionFactory(Executors.newFixedThreadPool(2));
	
	/**
	 * Only constructor.
	 * 
	 * @param tracker Provide the root of the Oddjob component hierarchy.
	 */
	public OddjobApiImpl(OddjobTracker tracker, File uploadDirectory) {
		this.tracker = tracker;
		
		this.uploadDirectory = uploadDirectory;
	}

	@Override
	public Response summariesFor(String componentPaths) {
		
		String[] componentPathsArray = componentPaths.split(",");
		
		ComponentSummary[] summaries = tracker.nodeIdFor(
				componentPathsArray);
		
		Gson gson = new Gson();
		String json = gson.toJson(summaries);  
	
		if (logger.isDebugEnabled()) {
			logger.debug("summariesFor(" + componentPaths + 
					"), Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
	}
	
	@Override
	public Response nodeInfo(String nodeIds, long eventSeq) {

		Gson gson = new Gson();
		
		try {
			String[] nodeIdsStrArray = nodeIds.split(",");
			int[] nodeIdsArray = new int[nodeIdsStrArray.length];
			for (int i = 0; i < nodeIdsArray.length; ++i) {
				nodeIdsArray[i] = Integer.parseInt(nodeIdsStrArray[i]);
			}
			
			NodeInfos nodeInfos = tracker.infoFor(eventSeq, nodeIdsArray);
			
			String json = gson.toJson(nodeInfos);  
		
			if (logger.isDebugEnabled()) {
				logger.debug("nodeInfo(" + nodeIds + ", " + eventSeq + 
						"), Response: " + json);
			}
			
			return Response.status(200).entity(json).build();
		}
		catch (Exception e) {
			return Response.status(400).entity(gson.toJson(
					ExceptionBean.createFrom(e))).build();
		}
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
		
		Gson gson = new Gson();
		
		try {
			Object node = tracker.nodeFor(Integer.parseInt(nodeId));
		
			ActionBean[] actions = ActionBean.createManyFrom(
					actionFactory.actionsFor(node));
			
			String json = gson.toJson(actions);  
		
			if (logger.isDebugEnabled()) {
				logger.debug("actionsFor(" + nodeId+ 
						"), Response: " + json);
			}
			
			return Response.status(200).entity(json).build();
		}
		catch (Exception e) {
			return Response.status(400).entity(gson.toJson(
					ExceptionBean.createFrom(e))).build();
		}
	}
	
	@Override
	public Response performAction(String nodeId, String actionName) {
		
		Gson gson = new Gson();
		
		try {
			Object node = nodeFor(nodeId);
			
			ActionStatus status = actionFactory.performAction(
					node, actionName, null);
			
			String json = gson.toJson(status);  
			
			if (logger.isDebugEnabled()) {
				logger.debug("performAction(" + nodeId+ 
						"), Response: " + json);
			}
			
			return Response.status(200).entity(json).build();
		}		
		catch (Exception e) {
			return Response.status(400).entity(gson.toJson(
					ExceptionBean.createFrom(e))).build();
		}
	}
	
	@Override
	public Response dialogFor(String nodeId, String actionName) {
		
		Gson gson = new Gson();
		
		try {
			Object node = nodeFor(nodeId);
			
			WebDialog dialog = actionFactory.dialogFor(node, actionName);
			
			String json = gson.toJson(dialog);  
			
			if (logger.isDebugEnabled()) {
				logger.debug("formFor(" + nodeId+ 
						"), Response: " + json);
			}
			
			return Response.status(200).entity(json).build();
		}		
		catch (Exception e) {
			return Response.status(400).entity(gson.toJson(
					ExceptionBean.createFrom(e))).build();
		}
	}
	
	@Override
	public Response actionForm2(String nodeId, String actionName,
			MultivaluedMap<String, String> formParams) {
		
		Gson gson = new Gson();
		
		try {
			Object node = nodeFor(nodeId);
			
			Properties properties = new Properties();
			
			for (String key : formParams.keySet()) {
				// Don't support multiple values yet...
				properties.setProperty(key, formParams.getFirst(key));
			}
		
			ActionStatus status = actionFactory.performAction(
					node, actionName, properties);
			
			String json = gson.toJson(status);  
		
			if (logger.isDebugEnabled()) {
				logger.debug("actionForm(" + nodeId+ 
						"), Response: " + json);
			}
			
			return Response.status(200).entity(json).build();
		}		
		catch (Exception e) {
			return Response.status(400).entity(gson.toJson(
					ExceptionBean.createFrom(e))).build();
		}
		
	}
	
	@Override
    public Response actionForm(String nodeId, String actionName,
    		HttpServletRequest request) {
		
		Gson gson = new Gson();
		
		try {
			Object node = nodeFor(nodeId);
			
			FormData formData = new MultipartRequestMap(uploadDirectory).parse(request);
			
			Properties properties = new Properties();
			
			for (String key : formData.getParameterNames()) {
				properties.setProperty(key, formData.getParameter(key));
			}
		
			ActionStatus status = actionFactory.performAction(
					node, actionName, properties);
			
			String json = gson.toJson(status);  
		
			if (logger.isDebugEnabled()) {
				logger.debug("actionForm(" + nodeId+ 
						"), Response: " + json);
			}
			
			return Response.status(200).entity(json).build();
		}		
		catch (Exception e) {
			
			String json = gson.toJson(
					ExceptionBean.createFrom(e));
			
			if (logger.isDebugEnabled()) {
				logger.debug("actionForm(" + nodeId+ 
						"), Response: " + json);
			}
			
			return Response.status(400).entity(json).build();
		}
    }	
	
	@Override
	public Response state(String nodeId) {
		StateDTO stateEvent = tracker.stateFor(Integer.parseInt(nodeId));
		
		Gson gson = new Gson();
		String json = gson.toJson(stateEvent);  
	
		if (logger.isDebugEnabled()) {
			logger.debug("state(" + nodeId + 
					"), Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
	}
	
	@Override
	public Response consoleLines(String nodeId, String logSeq) {
		
		LogLines logLines = tracker.consoleLinesFor(
				Integer.parseInt(nodeId),
				Long.parseLong(logSeq));
		
		Gson gson = new Gson();
		String json = gson.toJson(logLines);  
	
		if (logger.isDebugEnabled()) {
			logger.debug("consoleLines(" + nodeId+ ", " + logSeq +
					"), Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
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
	
	@Override
	public Response properties(String nodeId) {
		PropertiesDTO propertiesEvent = tracker.propertiesFor(Integer.parseInt(nodeId));
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		String json = gson.toJson(propertiesEvent);  
	
		if (logger.isDebugEnabled()) {
			logger.debug("properties(" + nodeId + 
					"), Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
	}
	
	protected Object nodeFor(String nodeIdString) {
		int nodeId;
		try {
			nodeId = Integer.parseInt(nodeIdString);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Failed parsing Node Id [" + 
					nodeIdString + "]: " + e.getMessage());
		}
		
		Object node = tracker.nodeFor(nodeId);
		
		if (node == null) {
			throw new IllegalArgumentException("Node does not exist.");
		}
		return node;
	}
	
}
