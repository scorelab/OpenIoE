package com.scorelab.ioe.security.communication.service.impl;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.scorelab.ioe.security.communication.exceptions.InvalidResponseException;
import com.scorelab.ioe.security.communication.service.api.POSTService;

public class POSTServiceBean implements POSTService {

	@Override
	public String post(String server, String path, LinkedHashMap<String, Object> parameters) throws InvalidResponseException {
		String json =  null;
		Client client = null;
		try {
			ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder()
					.establishConnectionTimeout(100, TimeUnit.SECONDS)
					.socketTimeout(100, TimeUnit.SECONDS);
			clientBuilder = clientBuilder.connectionPoolSize(1);
			client = clientBuilder.build();
			WebTarget webResource = client.target(server);
			webResource = webResource.path(path);
			Builder builder = webResource.request(MediaType.APPLICATION_JSON);
			builder.accept(MediaType.APPLICATION_JSON);
			Response response = builder.post(Entity.json(parameters));
			if (response.getStatus() == Status.OK.getStatusCode()) {
				json = response.readEntity(String.class);
			} else {
				throw new InvalidResponseException("Response code is not 200.");
			}
		} catch (Exception ex) {
			throw new InvalidResponseException("POST error when calling server: " + server + " path: " + path + ". " + ex.getMessage());
		} finally {
			if(client != null) {
				client.close();
			}
		}
		return json;
	}

}