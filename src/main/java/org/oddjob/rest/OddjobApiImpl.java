package org.oddjob.rest;

import javax.ws.rs.core.Response;

import org.oddjob.rest.model.NodeInfos;
import org.oddjob.rest.model.OddjobTracker;

import com.google.gson.Gson;

public class OddjobApiImpl implements OddjobApi {

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
	
		 return Response.status(200).entity(json).build();
	}
	
}
