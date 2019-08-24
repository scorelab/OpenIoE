package com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoManagerService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService.Level;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;

public class ConsumerLoopUnregister extends ConsumerLoopGeneric {
	@EJB
	private CryptoService cryptoService;

	@EJB
	private CryptoManagerService cryptoManager;
	
	@EJB
	private LoggingService loggingService;

	public ConsumerLoopUnregister() {
		super(CryptoIntegrationConstants.TOPIC_UNREGISTER, "cryptointegrationgroup", "cryptointegrationunregister");
	}

	@Override
	public void process(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sessionId = jsonObject.getString("sessionId");
			Boolean result = cryptoService.unregisterCC(sessionId);
			cryptoManager.put("unregister." + sessionId, result);
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}

}