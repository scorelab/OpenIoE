package com.scorelab.ioe.security.crypto.cryptoejb.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.scorelab.ioe.security.crypto.cryptoejb.dao.api.ConfigDAO;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.ConfigService;

@Stateless
public class ConfigServiceImpl implements ConfigService {

	@EJB 
	private ConfigDAO configDAO;

	@Override
	public String findParameterByKey(String key) {
		return configDAO.findParameterByKey(key);
	}

}
