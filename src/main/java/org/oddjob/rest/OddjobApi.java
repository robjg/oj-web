package org.oddjob.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/ojws")
public interface OddjobApi {

	@GET
	@Path("nodeInfo")
	@Produces("application/json")
	public Response nodeInfo(@QueryParam("nodeIds") String nodeIds, 
			@QueryParam("eventSeq") long eventSeq);
}
