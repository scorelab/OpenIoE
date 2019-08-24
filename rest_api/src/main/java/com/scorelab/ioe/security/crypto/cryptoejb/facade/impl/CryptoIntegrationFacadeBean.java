package com.scorelab.ioe.security.crypto.cryptoejb.facade.impl;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import com.scorelab.ioe.security.crypto.cryptoejb.facade.api.CryptoIntegrationFacade;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.ConfigService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoService;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CryptoIntegrationFacadeBean implements CryptoIntegrationFacade {
	
	@EJB
	private CryptoService cryptoService;

	@EJB
	private ConfigService configService;

	@Override
	public Map<String, String> encrypt(String key, String iv, int tagSize, String data) {
		return cryptoService.encrypt(key, iv, tagSize, data);
	}

	@Override
	public Map<String, String> decrypt(String key, String iv, int tagSize, String data) {
		return cryptoService.decrypt(key, iv, tagSize, data);
	}

	@Override
	public Map<String, String> encryptWithCC(String sessionId, String data) {
		return cryptoService.encryptWithCC(sessionId, data);
	}

	@Override
	public Map<String, String> decryptWithCC(String sessionId, String data) {
		return cryptoService.decryptWithCC(sessionId, data);
	}

	@Override
	public boolean registerCC(String sessionId, String transactionId, String provider, int tagLen, String keyServerToComponent,
			String keyComponentToServer, String ivServerToComponent, String ivComponentToServer, long lifespan) {
		return cryptoService.registerCC(sessionId, transactionId, provider, tagLen, keyServerToComponent, keyComponentToServer, ivServerToComponent, ivComponentToServer, lifespan);
	}

	@Override
	public boolean unregisterCC(String sessionId) {
		return cryptoService.unregisterCC(sessionId);
	}

	@Override
	public String findParameterByKey(String key) {
		return configService.findParameterByKey(key);
	}

}
