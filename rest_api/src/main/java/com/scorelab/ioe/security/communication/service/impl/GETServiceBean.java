package com.scorelab.ioe.security.communication.service.impl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.scorelab.ioe.security.communication.exceptions.InvalidResponseException;
import com.scorelab.ioe.security.communication.service.api.GETService;

public class GETServiceBean implements GETService {

	@Override
	public String get(String server, String path) throws InvalidResponseException {
		String json =  null;
		Client client = null;
		try {
			client = ClientBuilder.newBuilder().build();
			WebTarget webResource = client.target(server);
			webResource = webResource.path(path);
			Builder builder = webResource.request(MediaType.APPLICATION_JSON);
			builder.accept(MediaType.APPLICATION_JSON);
			Response response = builder.get();
			if (response.getStatus() == Status.OK.getStatusCode()) {
				json = response.readEntity(String.class);
			} else {
				throw new InvalidResponseException("Response code is not 200.");
			}
		} catch (Exception ex) {
			throw new InvalidResponseException("GET error when calling server: " + server + " path: " + path + ". " + ex.getMessage());
		} finally {
			if(client != null) {
				client.close();
			}
		}
		return json;
	}

}