package com.scorelab.ioe.security.crypto.cryptoejb.dao.api;

import javax.ejb.Local;

@Local
public interface CryptoManagerDAO {
	
	void add(String sessionId, String json);
	
	void add(String sessionId, String json, Long lifespan);

	void remove(String sessionId);
	
	String get(String sessionId);
}
