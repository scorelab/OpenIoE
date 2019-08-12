package com.scorelab.ioe.security.crypto.cryptoejb.service.api;

import javax.ejb.Local;

import com.scorelab.ioe.security.crypto.cryptoejb.beans.cache.CryptoChannelStateCache;

@Local
public interface CryptoChannelService {

	void save(String sessionId, CryptoChannelStateCache cache);
	
	void save(String sessionId, CryptoChannelStateCache cache, Long lifespan);
	
	void remove(String sessionId);
	
	CryptoChannelStateCache findBySessionId(String sessionId);
}