package com.scorelab.ioe.security.communication.service.api;

import java.util.LinkedHashMap;

import com.scorelab.ioe.security.communication.exceptions.InvalidResponseException;

public interface POSTService {
	
	String post(String server, String path, LinkedHashMap<String, Object> parameters) throws InvalidResponseException;
}