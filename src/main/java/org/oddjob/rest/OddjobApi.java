package org.oddjob.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/api")
public interface OddjobApi {

	@GET
	@Path("nodeInfo")
	@Produces("application/json")
	public Response nodeInfo(@QueryParam("nodeIds") String nodeIds, 
			@QueryParam("eventSeq") long eventSeq);
	
	@GET
	@Path("icon/{iconId}")
	@Produces("image/gif")
	public Response iconFor(@PathParam("iconId") String iconId);
	
	@GET
	@Path("actionsFor/{nodeId}")
	@Produces("application/json")
	public Response actionsFor(@PathParam("nodeId") String nodeId);
	
	@GET
	@Path("action/{actionName}/{nodeId}")
	public void performAction(@PathParam("actionName") String actionName,
			@PathParam("nodeId") String nodeId);
	
	@GET
	@Path("action/{actionName}/{nodeId}")
	@Produces("application/json")
	public Response logLines(
			@PathParam("nodeId") String nodeId,
			@QueryParam("logSeq") String logSeq);
}
