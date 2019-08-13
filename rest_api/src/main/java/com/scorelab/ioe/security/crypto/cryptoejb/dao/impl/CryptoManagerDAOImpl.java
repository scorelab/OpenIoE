package com.scorelab.ioe.security.crypto.cryptoejb.dao.impl;

import javax.ejb.Singleton;

import com.scorelab.ioe.security.crypto.cryptoejb.dao.api.CryptoManagerDAO;

@Singleton
public class CryptoManagerDAOImpl extends GenericRedisDAOImpl implements CryptoManagerDAO {

	private String PREFFIX_CRYPTO_MANAGER_CACHE = "cryptomanager.";

	@Override
	public void add(String sessionId, String json) {
		super.set(PREFFIX_CRYPTO_MANAGER_CACHE + sessionId, json);
	}

	@Override
	public void remove(String sessionId) {
		super.del(PREFFIX_CRYPTO_MANAGER_CACHE + sessionId);
	}

	@Override
	public String get(String sessionId) {
		return super.get(PREFFIX_CRYPTO_MANAGER_CACHE + sessionId);
	}

}
