package com.scorelab.ioe.security.crypto.cryptoejb.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONObject;

import com.scorelab.ioe.security.crypto.cryptoejb.beans.cache.CryptoManagerCache;
import com.scorelab.ioe.security.crypto.cryptoejb.dao.api.CryptoManagerDAO;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoManagerService;

@Stateless
public class CryptoManagerServiceImpl implements CryptoManagerService {
	
	@EJB
	private CryptoManagerDAO cryptoManagerDAO;

	@Override
	public void put(String sessionId, String plainText, String cypherText, String results) {
		CryptoManagerCache cache = new CryptoManagerCache();
		cache.setCipherText(cypherText);
		cache.setPlainText(plainText);
		cache.setResults(results);
		cache.setResultsOperation(null);
		JSONObject jsonObject = new JSONObject(cache);
		cryptoManagerDAO.add(sessionId, jsonObject.toString());
	}

	@Override
	public void put(String sessionId, Boolean results) {
		CryptoManagerCache cache = new CryptoManagerCache();
		cache.setCipherText("");
		cache.setPlainText("");
		cache.setResults("");
		cache.setResultsOperation(results);
		JSONObject jsonObject = new JSONObject(cache);
		cryptoManagerDAO.add(sessionId, jsonObject.toString());
	}

	@Override
	public void remove(String sessionId) {
		cryptoManagerDAO.remove(sessionId);
	}
	
}
