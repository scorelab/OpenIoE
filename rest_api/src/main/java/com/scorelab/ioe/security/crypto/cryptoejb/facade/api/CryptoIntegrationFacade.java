package com.scorelab.ioe.security.crypto.cryptoejb.facade.api;

import java.util.Map;

import javax.ejb.Local;

@Local
public interface CryptoIntegrationFacade {
	
	Map<String, String> encrypt(String key, String iv, int tagSize, String data);

	Map<String, String> decrypt(String key, String iv, int tagSize, String data);

	Map<String, String> encryptWithCC(String sessionId, String data);

	Map<String, String> decryptWithCC(String sessionId, String data);

	boolean registerCC(String sessionId, String transactionId, String provider, int tagLen, String keyServerToComponent, String keyComponentToServer,
			String ivServerToComponent, String ivComponentToServer, long lifespan);

	boolean unregisterCC(String sessionId);
	
	String findParameterByKey(String key);
}
