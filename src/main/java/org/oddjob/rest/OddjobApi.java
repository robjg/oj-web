package org.oddjob.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Definition of the Oddjob Restful Web Service.
 * 
 * @author rob
 *
 */
@Path("/api")
public interface OddjobApi {

	@GET
	@Path("summariesFor")
	@Produces("application/json")
	Response summariesFor(@QueryParam("paths") String componentPaths);
	
	@GET
	@Path("nodeInfo")
	@Produces("application/json")
	Response nodeInfo(@QueryParam("nodeIds") String nodeIds,
					  @QueryParam("eventSeq") long eventSeq);
	
	@GET
	@Path("icon/{iconId}")
	@Produces("image/gif")
	Response iconFor(@PathParam("iconId") String iconId);
	
	@GET
	@Path("actionsFor/{nodeId}")
	@Produces("application/json")
	Response actionsFor(@PathParam("nodeId") String nodeId);
	
	@GET
	@Path("action/{nodeId}/{actionName}")
	@Produces("application/json")
	Response performAction(@PathParam("nodeId") String nodeId,
						   @PathParam("actionName") String actionName);
	
	@GET
	@Path("dialogFor/{nodeId}/{actionName}")
	@Produces("application/json")
	Response dialogFor(@PathParam("nodeId") String nodeId,
					   @PathParam("actionName") String actionName);
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("formAction/{nodeId}/{actionName}")
	@Produces("application/json")
	Response actionForm2(@PathParam("nodeId") String nodeId,
						 @PathParam("actionName") String actionName,
						 MultivaluedMap<String, String> formParams);

    @POST
	@Path("formAction/{nodeId}/{actionName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	Response actionForm(@PathParam("nodeId") String nodeId,
						@PathParam("actionName") String actionName,
						@Context HttpServletRequest request);
    
    @GET
	@Path("state/{nodeId}")
	@Produces("application/json")
	Response state(
			@PathParam("nodeId") String nodeId);

	@GET
	@Path("consoleLines/{nodeId}")
	@Produces("application/json")
	Response consoleLines(
			@PathParam("nodeId") String nodeId,
			@QueryParam("logSeq") String logSeq);
	
	@GET
	@Path("logLines/{nodeId}")
	@Produces("application/json")
	Response logLines(
			@PathParam("nodeId") String nodeId,
			@QueryParam("logSeq") String logSeq);
	
	@GET
	@Path("properties/{nodeId}")
	@Produces("application/json")
	Response properties(
			@PathParam("nodeId") String nodeId);

}
