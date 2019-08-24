package com.scorelab.ioe.security.communication.service.api;

import com.scorelab.ioe.security.communication.exceptions.InvalidResponseException;

public interface GETService {
	
	String get(String server, String path) throws InvalidResponseException;
}