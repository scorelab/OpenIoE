package com.scorelab.ioe.security.crypto.cryptoejb.dao.impl;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.scorelab.ioe.security.crypto.cryptoejb.dao.api.ConfigDAO;

@Startup
@Singleton
public class ConfigDAOImpl extends GenericRedisDAOImpl implements ConfigDAO {

	@Override
	public String findParameterByKey(String key) {
		return super.get(key);
	}
	
}
