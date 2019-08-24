package com.scorelab.ioe.security.communication.facade.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.scorelab.ioe.security.communication.exceptions.InvalidResponseException;
import com.scorelab.ioe.security.communication.facade.api.CommunicationFacade;
import com.scorelab.ioe.security.communication.service.api.GETService;
import com.scorelab.ioe.security.communication.service.api.KafkaBrokersService;
import com.scorelab.ioe.security.communication.service.api.NodeConfigsService;
import com.scorelab.ioe.security.communication.service.api.POSTService;
import com.scorelab.ioe.security.communication.service.api.RedisSentinelService;
import com.scorelab.ioe.security.communication.service.impl.GETServiceBean;
import com.scorelab.ioe.security.communication.service.impl.KafkaBrokersServiceBean;
import com.scorelab.ioe.security.communication.service.impl.NodeConfigsServiceBean;
import com.scorelab.ioe.security.communication.service.impl.POSTServiceBean;
import com.scorelab.ioe.security.communication.service.impl.RedisSentinelServiceBean;

public class CommunicationFacadeBean implements CommunicationFacade {
	@Override
	public String requestKafkaBrokers() {
		KafkaBrokersService kafkaBrokersService = new KafkaBrokersServiceBean();
		return kafkaBrokersService.requestKafkaBrokers();
	}
	
	@Override
	public Set<String> requestRedisSentinelsHosts() {
		RedisSentinelService redisSentinelService = new RedisSentinelServiceBean();
		return redisSentinelService.requestRedisSentinelsHosts();
	}

	@Override
	public LinkedHashMap<String, Object> requestNodeConfigs() {
		NodeConfigsService nodeConfigService = new NodeConfigsServiceBean();
		return nodeConfigService.requestNodeConfigs();
	}

	@Override
	public String get(String server, String path) throws InvalidResponseException {
		GETService getService = new GETServiceBean();
		return getService.get(server, path);
	}
	
	@Override
	public String get(String target, String find, LinkedHashMap<String, Object> microservice) throws InvalidResponseException {
		JSONObject service = new JSONObject(post(target, find, microservice));
		return get(service.getString("target"), service.getString("path"));
	}

	@Override
	public String post(String server, String path, LinkedHashMap<String, Object> parameters) throws InvalidResponseException {
		POSTService postService = new POSTServiceBean();
		return postService.post(server, path, parameters);
	}

	@Override
	public String post(String target, String find, LinkedHashMap<String, Object> microservice, LinkedHashMap<String, Object> parameters) throws InvalidResponseException {
		JSONObject service = new JSONObject(post(target, find, microservice));
		return post(service.getString("target"), service.getString("path"), parameters);
	}
}