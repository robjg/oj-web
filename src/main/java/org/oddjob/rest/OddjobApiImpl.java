package org.oddjob.rest;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.oddjob.rest.model.NodeInfos;
import org.oddjob.rest.model.OddjobTracker;

import com.google.gson.Gson;

public class OddjobApiImpl implements OddjobApi {

	private static final Logger logger = Logger.getLogger(OddjobApiImpl.class);
	
	private final OddjobTracker tracker;
		
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
			logger.debug("Request(" + nodeIds + ", " + eventSeq + 
					") Response: " + json);
		}
		
		return Response.status(200).entity(json).build();
	}
	
	@Override
	public Response iconFor(String iconId) {
		
		return Response.ok(tracker.iconImageFor(iconId)).build();
	}
}
