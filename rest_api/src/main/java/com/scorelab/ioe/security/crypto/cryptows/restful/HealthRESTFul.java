package com.scorelab.ioe.security.crypto.cryptows.restful;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Named
@Path("/health")
public class HealthRESTFul {
	
	@GET
	@Path("/status")
	public Response status() {
		return Response.ok().build();
	}
}
