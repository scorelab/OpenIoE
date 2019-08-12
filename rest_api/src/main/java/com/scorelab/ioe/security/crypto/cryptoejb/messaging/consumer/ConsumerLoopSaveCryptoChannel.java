package com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoManagerService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService.Level;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;

public class ConsumerLoopSaveCryptoChannel extends ConsumerLoopGeneric {
	@EJB
	private CryptoService cryptoService;  

	@EJB
	private CryptoManagerService cryptoManager;
	
	@EJB
	private LoggingService loggingService;

	public ConsumerLoopSaveCryptoChannel() {
		super(CryptoIntegrationConstants.TOPIC_SAVE_CRYPTO_CHANNEL, "cryptointegrationgroup", "cryptointegrationsave");
	}

	@Override
	public void process(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sessionId = jsonObject.getString("sessionId");
			String transactionId =  jsonObject.getString("transactionId");
			Boolean result = cryptoService.saveCryptoChannel(sessionId, transactionId);
			cryptoManager.put(sessionId, result);
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
		
	}
	
}