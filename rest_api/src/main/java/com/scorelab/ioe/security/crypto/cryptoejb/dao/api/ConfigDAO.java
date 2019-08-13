package com.scorelab.ioe.security.crypto.cryptoejb.dao.api;

import javax.ejb.Local;

@Local
public interface ConfigDAO {
	
	String findParameterByKey(String key);
}
