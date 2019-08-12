package com.scorelab.ioe.security.crypto.cryptoejb.service.impl;

import javax.ejb.Stateless;

import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService;

@Stateless
public class LoggingServiceImpl implements LoggingService {

	@Override
	public void saveLogging(Level level, String component, String username, String details) {
		System.out.printf("[%s] %s. %s. %s.\n", level.toString(), component, username, details);
	}
	
}
